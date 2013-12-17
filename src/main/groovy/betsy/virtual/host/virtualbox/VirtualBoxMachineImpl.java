package betsy.virtual.host.virtualbox;

import betsy.virtual.host.VirtualBoxException;
import betsy.virtual.host.VirtualBoxMachine;
import betsy.virtual.host.exceptions.vm.VBoxExceptionCode;
import betsy.virtual.host.virtualbox.utils.Timeouts;
import org.apache.log4j.Logger;
import org.virtualbox_4_2.*;

import java.util.Objects;
import java.util.Set;

/**
 * The {@link VirtualBoxMachineImpl} represents a virtual machine running on
 * VirtualBox. It can be started, stopped and snapshots may be created of its
 * current state.
 *
 * @author Cedric Roeck  
 * @version 1.0
 */
public class VirtualBoxMachineImpl implements VirtualBoxMachine {

    private final IMachine machine;
    private final VirtualBoxManager vbManager;
    private ISession session;

    /**
     * if true, the {@link VirtualBoxMachineImpl} will be running in the
     * background and no GUI window will appear.
     */
    private boolean headless;

    private static final Logger log = Logger.getLogger(VirtualBoxMachineImpl.class);

    public VirtualBoxMachineImpl(VirtualBoxManager vbManager, IMachine machine) {
        this.machine = Objects.requireNonNull(machine);
        this.vbManager = Objects.requireNonNull(vbManager);
        this.session = vbManager.getSessionObject();
    }

    /**
     * Start the {@link IMachine}. VirtualBox currently supports three different
     * states. Two of them are showing the GUI, one is saving the resources,
     * hides the GUI and therefore is 'headless'.<br/>
     * <br>
     * The {@link IMachine} should not be running before.
     */
    public void start() {
        log.trace("Starting VM");
        if (isActive()) {
            log.warn("Can't start VM, is already active. Aborting.");
            return;
        }

        startVirtualMachine();
    }

    private void startVirtualMachine() {
        IProgress startProgress;
        if (headless) {
            startProgress = machine.launchVMProcess(session, "headless", null);
        } else {
            startProgress = machine.launchVMProcess(session, "gui", null);
        }

        waitForCompletion(startProgress, Timeouts.MINUTE);
    }

    private void waitForCompletion(IProgress progress, int timeout) {
        if (!progress.getCompleted()) {
            progress.waitForCompletion(timeout);
        }
    }

    /**
     * Stopping the {@link IMachine} and causing the VirtualBox's VM to be in
     * 'PoweredOff' state.<br>
     * If the VM is still running after the timeout, for instance of the stop
     * caused a lock, it will be killed using VirtualBox's emergency kill
     * switch.<br>
     * <br>
     * Should only be used of the {@link IMachine} is in a running state.
     */
    public void stop() {
        log.trace("Stopping VM");
        try {
            if (isRunning()) {
                waitForCompletion(session.getConsole().powerDown(), Timeouts.TEN_SECONDS);
            }
        } catch (VBoxException e) {
            if (VBoxExceptionCode.valueOf(e).equals(VBoxExceptionCode.VBOX_E_INVALID_VM_STATE)) {
                log.warn("Could not power off, VM was in invalid state:", e);
            } else {
                log.fatal("Unexpected VBoxException while stopping VM:", e);
            }
        } finally {
            // verify if stopped, else kill
            killAndCleanup();
        }
        log.trace("finished stop method");
    }

    /**
     * Save the current running state of the {@link IMachine}. After saving the
     * state the {@link IMachine} won't be running anymore.<br>
     * It can be used as an alternative to {@link #stop()}. Nevertheless is also takes
     * significantly longer. <br>
     * <br>
     * Should only be used of the {@link IMachine} is in a running state.
     */
    public void saveState() {
        log.trace("Saving VM state");
        try {
            if (isRunning()) {
                waitForCompletion(session.getConsole().saveState(), Timeouts.THIRTY_SECONDS);
            }
        } catch (VBoxException e) {
            if (VBoxExceptionCode.valueOf(e).equals(VBoxExceptionCode.VBOX_E_INVALID_VM_STATE)) {
                log.warn("Could not save VM state, was in invalid state:", e); // ignore
            } else {
                throw e; // unknown
            }
        } finally {
            killAndCleanup();

        }
    }

    private void killAndCleanup() {
        // verify if stopped, else kill
        if (isActive()) {
            kill();
        }
        try {
            session.unlockMachine();
        } catch (VBoxException exception) {
            // ignore if was not locked
        }
    }

    /**
     * Killing the {@link IMachine} using the 'emergencystop'.<br>
     * Stops the VM in nearly every situation but might also cause data loss or
     * corrupted states. <br>
     * <br>
     * Should only be used of the {@link IMachine} is in a running state.
     */
    public void kill() {
        log.debug("killing machine");
        machine.launchVMProcess(session, "emergencystop", null);
        try {
            session.unlockMachine();
        } catch (VBoxException exception) {
            // ignore if was not locked
            log.trace("Unlocking session after kill failed:", exception);
        }
    }

    /**
     * Check whether the {@link IMachine} is in an active running state.
     * Therefore the State must be either Running, Paused or Stuck (after an
     * severe error).<br>
     * If the VM is running it can be stopped.
     *
     * @return true if the VM is running, paused or stuck
     */
    public boolean isRunning() {
        try {
            MachineState state = this.machine.getState();
            if (state.equals(MachineState.Running)) {
                return true;
            }
            if (state.equals(MachineState.Paused)) {
                return true;
            }
            if (state.equals(MachineState.Stuck)) {
                return true;
            }
        } catch (VBoxException exception) {
            // in error cases the state can't be always read.
            log.error("Couldn't determine running state:", exception);
        }
        return false;
    }

    /**
     * Check whether the {@link IMachine} is in a active state.<br>
     * Even if this method is very similar to {@link #isRunning()}, the
     * difference is that every mid-state such as saving, powering-off, etc. is
     * still an active state, even if the VM is currently not responding to the
     * User's input.
     *
     * @return true if the VM is active
     */
    public boolean isActive() {
        try {
            return !isInactive();
        } catch (VBoxException exception) {
            // in error cases the state can't be always read.
            log.warn("Couldn't determine active state:", exception);
        }
        return false;
    }

    private boolean isInactive() {
        MachineState state = this.machine.getState();
        log.debug("Current VM State: " + state.toString());
        return MachineState.Aborted.equals(state) || MachineState.PoweredOff.equals(state) || MachineState.Saved.equals(state);
    }


    /**
     * Resetting the {@link IMachine} to the latest {@link ISnapshot}.<br>
     * <br>
     * Requires the {@link IMachine} to have at least one {@link ISnapshot}.
     */
    public void restore() {
        ISession subSession = null;
        try {
            log.debug("Resetting " + machine.getName() + " to latest snapshot");

            subSession = vbManager.getSessionObject();
            machine.lockMachine(subSession, LockType.Write);
            IConsole console = subSession.getConsole();

            ISnapshot snapshot = machine.getCurrentSnapshot();
            if (snapshot == null) {
                throw new IllegalStateException("Can't reset the VM to the "
                        + "latest snapshot if the VM does not have any "
                        + "snapshot.");
            }

            waitForCompletion(console.restoreSnapshot(snapshot), Timeouts.THIRTY_SECONDS);

            log.trace("State after restoration:" + machine.getState());
            log.trace("restored VM to latest snapshot with name [" + snapshot.getName() + "]");
        } finally {
            log.trace("trigger finally block after reset snapshot");
            if (subSession != null) {
                try {
                    subSession.unlockMachine();
                } catch (VBoxException exception) {
                    // ignore if was not locked
                    log.warn("Unlocking subSession after restoration failed:",
                            exception);
                }
            }
        }
    }

    @Override
    public void setHeadlessMode(boolean headless) {
        this.headless = headless;
    }

    @Override
    public void applyPortForwarding(Set<Integer> forwardingPorts) throws VirtualBoxException {
        new PortForwardingConfigurator(this.machine).applyPortForwarding(forwardingPorts);
    }

    /**
     * Taking a snapshot of this {@link betsy.virtual.host.virtualbox.VirtualBoxMachineImpl} based on the current
     * state.<br>
     * A bug in VirtualBox (https://www.virtualbox.org/ticket/9255) forces us to
     * pause the VM before taking the snapshot. Afterwards we will always resume
     * the VM, even if it was already paused before.
     *
     * @param name the name of the snapshot to be created
     * @param desc the description of the snapshot to be created
     */
    public void takeSnapshot(final String name, final String desc) {
        log.debug("Taking VM snapshot");
        IConsole console = session.getConsole();

        log.debug("Pausing VM before taking a snapshot");
        console.pause();

        waitForCompletion(console.takeSnapshot(name, desc), Timeouts.THIRTY_SECONDS);

        // before resuming make sure the snapshot has been saved
        while (this.machine.getState().equals(MachineState.Saving)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        log.debug("Resuming VM after taking a snapshot");
        console.resume();
    }

    /**
     * Check whether the VM has at least one {@link ISnapshot} that is marked as
     * 'online' and therefore contains an already started system.
     *
     * @return true if VM contains at least one 'online' snapshot
     */
    public boolean hasRunningSnapshot() {
        ISnapshot snapshot = machine.getCurrentSnapshot();
        if (snapshot != null) {
            // online snapshot?
            return snapshot.getOnline();
        }
        return false;
    }

}

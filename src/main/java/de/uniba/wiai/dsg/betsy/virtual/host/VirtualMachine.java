package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;
import org.virtualbox_4_2.IConsole;
import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.INATEngine;
import org.virtualbox_4_2.INetworkAdapter;
import org.virtualbox_4_2.IProgress;
import org.virtualbox_4_2.ISession;
import org.virtualbox_4_2.ISnapshot;
import org.virtualbox_4_2.LockType;
import org.virtualbox_4_2.MachineState;
import org.virtualbox_4_2.NATProtocol;
import org.virtualbox_4_2.SessionState;
import org.virtualbox_4_2.VBoxException;
import org.virtualbox_4_2.VirtualBoxManager;

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortRedirectException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortUsageException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.engine.VirtualizedEngineServiceException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualBoxExceptionCode;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceAddress;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.ServiceValidator;

//TODO Javadoc
public class VirtualMachine {

	private final Configuration config = Configuration.getInstance();

	private final IMachine machine;
	private final VirtualBoxManager vbManager;
	private ISession session;

	private final Logger log = Logger.getLogger(getClass());

	public VirtualMachine(VirtualBoxManager vbManager, IMachine machine) {
		this.machine = Objects.requireNonNull(machine);
		this.vbManager = Objects.requireNonNull(vbManager);
		this.session = vbManager.getSessionObject();
	}

	public void start(final boolean headless) {
		log.debug("VM state before start: " + machine.getState().toString());
		if (!isActive()) {
			IProgress startProgress = null;
			if (headless) {
				startProgress = machine.launchVMProcess(session, "headless",
						null);
			} else {
				startProgress = machine.launchVMProcess(session, "gui", null);
			}
			if (!startProgress.getCompleted()) {
				log.debug("Waiting for VM start");
				startProgress.waitForCompletion(15000);
			}
		} else {
			log.warn("Can't start VM, is already running");
		}
	}

	public void stop() {
		log.trace("Stop VM");
		try {
			if (isRunning()) {
				IConsole console = session.getConsole();
				IProgress stopProgress = console.powerDown();
				stopProgress.waitForCompletion(10000);
			}
		} catch (VBoxException exception) {
			if (VirtualBoxExceptionCode.valueOf(exception).equals(
					VirtualBoxExceptionCode.VBOX_E_INVALID_VM_STATE)) {
				// ignore
				log.warn("Could not power off VM, was in invalid state:",
						exception);
			} else {
				// unknown
				log.error("Unexpected VBoxException: ", exception);
				throw exception;
			}
		} finally {
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
	}

	public void saveState() {
		log.trace("Saving VM state");
		try {
			if (isRunning()) {
				IConsole console = session.getConsole();
				IProgress stopProgress = console.saveState();
				stopProgress.waitForCompletion(30000);
			}
		} catch (VBoxException exception) {
			if (VirtualBoxExceptionCode.valueOf(exception).equals(
					VirtualBoxExceptionCode.VBOX_E_INVALID_VM_STATE)) {
				// ignore
				log.warn("Could not save VM state, was in invalid state:",
						exception);
			} else {
				// unknown
				log.error("Unexpected VBoxException: ", exception);
				throw exception;
			}
		} finally {
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
	}

	public void kill() {
		machine.launchVMProcess(session, "emergencystop", null);
	}

	// Running in sense of stoppable
	public boolean isRunning() {
		try {
			MachineState state = this.machine.getState();
			log.debug("Is VM running? State: " + state.toString());

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
			log.warn("Couldn't determine running state:", exception);
		}
		return false;
	}

	// Every state where the gui is still opened
	public boolean isActive() {
		try {
			MachineState state = this.machine.getState();
			log.debug("Is VM active? State: " + state.toString());

			if (state.equals(MachineState.Aborted)) {
				return false;
			} else if (state.equals(MachineState.PoweredOff)) {
				return false;
			} else if (state.equals(MachineState.Saved)) {
				return false;
			} else {
				// in every other state the VM is active
				return true;
			}
		} catch (VBoxException exception) {
			// in error cases the state can't be always read.
			log.warn("Couldn't determine active state:", exception);
		}
		return false;
	}

	public boolean containsRunningSnapshot() {
		ISnapshot snapshot = machine.getCurrentSnapshot();
		if (snapshot != null) {
			// online snapshot?
			return snapshot.getOnline();
		}
		return false;
	}

	public ISnapshot takeSnapshot(final String name, final String desc) {
		IConsole console = session.getConsole();

		// TODO must be in running state to pause
		log.debug("Pause VM before taking snapshot");
		console.pause();

		IProgress snapshotProgress = console.takeSnapshot(name, desc);
		while (!snapshotProgress.getCompleted()) {
			log.trace("wait for snapshot completion");
			snapshotProgress.waitForCompletion(5000);
		}

		// before resuming make sure the snapshot has been saved
		while (this.machine.getState().equals(MachineState.Saving)) {
			log.trace("wait, VirtualBox is still saving... "
					+ this.machine.getState().toString());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		// TODO why resume?
		// TODO resume only if was in Running state
		console.resume();

		// get latestSnapshot and return
		return this.machine.getCurrentSnapshot();
	}

	public void resetToLatestSnapshot() {
		ISession subSession = null;
		try {
			log.debug("Resetting " + machine.getName() + " to latest snapshot");

			IConsole console = null;
			// if (machine.getSessionState().equals(SessionState.Unlocked)
			// || machine.getSessionState().equals(SessionState.Unlocking)) {
			log.info("Using subSession now");
			subSession = vbManager.getSessionObject();
			machine.lockMachine(subSession, LockType.Write);
			console = subSession.getConsole();
			// } else {
			// console = session.getConsole();
			// }

			ISnapshot snapshot = machine.getCurrentSnapshot();
			if (snapshot == null) {
				throw new IllegalStateException(
						"Can't reset the VM to the latest "
								+ "snapshot if the VM does not have any snapshot.");
			}

			IProgress snapshotProgress = console.restoreSnapshot(snapshot);

			// TODO include realistic timeout
			while (!snapshotProgress.getCompleted()) {
				log.trace("wait on restoring completion");
				snapshotProgress.waitForCompletion(5000);
			}

			log.trace("Snapshot restored state: "
					+ machine.getState().toString());

		} finally {
			if (subSession != null
					&& subSession.getState().equals(SessionState.Locked)) {
				subSession.unlockMachine();
			}
		}
	}

	public void applyPortForwardingWS(final Set<Integer> forwardingPorts)
			throws PortRedirectException {
		if (!isAlreadyRedirected(forwardingPorts)) {
			log.debug("Applying new port redirects...");

			// remove old redirections first
			clearPortForwardingWS();

			// add bVMS port if not yet contained
			forwardingPorts.add(48888);

			INATEngine natEngine = getNATEngine();
			for (Integer port : forwardingPorts) {
				// assuming every service uses TCP, which is true for HTTP
				natEngine.addRedirect("", NATProtocol.TCP, "", port, "", port);
			}

			long timeout = 10000;
			long start = -System.currentTimeMillis();

			while (natEngine.getRedirects().size() != forwardingPorts.size()) {
				if (System.currentTimeMillis() + start > timeout) {
					throw new PortRedirectException("Could not set redirected "
							+ "ports within 10s");
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		} else {
			log.trace("All ports are already redirected, skip.");
		}
	}

	private boolean isAlreadyRedirected(final Set<Integer> forwardingPorts) {

		// TODO use

		// TODO verify

		return false;

		// // get redirects
		// INATEngine natEngine = getNATEngine();
		// List<String> redirects = natEngine.getRedirects();
		// for (String redirect : redirects) {
		// // resolve host and guest port
		// String[] rds = redirect.split(",");
		// String hostPort = rds[3];
		// String guestPort = rds[5];
		// // verify both are equal
		// if(hostPort.equals(guestPort)) {
		// // verify is in list
		// if(forwardingPorts.contains(hostPort)) {
		// // is ok, test next redirect
		// continue;
		// }
		// }
		// return false;
		// }
		//
		// // each required redirect is already created
		// return true;
	}

	public void applyPortForwarding(final Set<Integer> forwardingPorts)
			throws PortRedirectException {
		if (!isAlreadyRedirected(forwardingPorts)) {
			log.debug("Applying new port redirects...");

			// make sure to add the bVMS port
			forwardingPorts.add(48888);

			// remove old redirections first
			clearPortForwarding();

			INATEngine natEngine = getNATEngine();

			String vbpath = config.getValueAsString("virtualisation.vbox.path");
			String vboxManagePath = config
					.getValueAsString("virtualisation.vbox.vboxmanage");
			File vbm = new File(vbpath, vboxManagePath);

			Runtime r = Runtime.getRuntime();

			for (Integer port : forwardingPorts) {
				String[] cmd = { vbm.getAbsolutePath(), "modifyvm",
						machine.getName(), "--natpf1",
						",tcp,," + port + ",," + port };
				try {
					Process proc = r.exec(cmd);
					InputStream inStr = proc.getInputStream();
					BufferedReader buff = new BufferedReader(
							new InputStreamReader(inStr));
					String str;
					while ((str = buff.readLine()) != null) {
						log.debug("console output:" + str);
					}

				} catch (IOException exception) {
					log.warn("Can't add port redirect rule:", exception);
				}

			}

			long timeout = 10000;
			long start = -System.currentTimeMillis();

			while (natEngine.getRedirects().size() != forwardingPorts.size()) {
				if (System.currentTimeMillis() + start > timeout) {
					throw new PortRedirectException(
							"Could not set redirected ports within 10s");
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		} else {
			log.trace("All ports are already redirected, skip.");
		}
	}

	public void clearPortForwarding() throws PortRedirectException {
		log.debug("Deleting all existing port redirections");

		INATEngine natEngine = getNATEngine();
		for (String redirect : natEngine.getRedirects()) {
			String[] rds = redirect.split(",");
			String redirectName = rds[0];

			String vbpath = config.getValueAsString("virtualisation.vbox.path");
			String vboxManagePath = config
					.getValueAsString("virtualisation.vbox.vboxmanage");
			File vbm = new File(vbpath, vboxManagePath);

			Runtime r = Runtime.getRuntime();
			String[] cmd = { vbm.getAbsolutePath(), "modifyvm",
					machine.getName(), "--natpf1", "delete", redirectName };
			try {
				Process proc = r.exec(cmd);
				InputStream inStr = proc.getInputStream();
				BufferedReader buff = new BufferedReader(new InputStreamReader(
						inStr));
				String str;
				while ((str = buff.readLine()) != null) {
					log.debug("console output:" + str);
				}

			} catch (IOException exception) {
				log.warn("Can't delete port redirect rule:", exception);
			}
		}

		long timeout = 10000;
		long start = -System.currentTimeMillis();

		while (natEngine.getRedirects().size() != 0) {
			if (System.currentTimeMillis() + start > timeout) {
				throw new PortRedirectException(
						"Could not delete redirected ports within 10s");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	public void clearPortForwardingWS() throws PortRedirectException {
		log.debug("Deleting all existing port redirections");
		INATEngine natEngine = getNATEngine();
		for (String redirect : natEngine.getRedirects()) {
			String[] rds = redirect.split(",");
			String redirectName = rds[0];
			natEngine.removeRedirect(redirectName);
		}

		long timeout = 10000;
		long start = -System.currentTimeMillis();

		while (natEngine.getRedirects().size() != 0) {
			if (System.currentTimeMillis() + start > timeout) {
				throw new PortRedirectException("Could not delete all "
						+ "redirected ports within 10s");
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private INATEngine getNATEngine() {
		// first adapter is always the NAT adapter
		INetworkAdapter na = machine.getNetworkAdapter(0l);
		return na.getNATEngine();
	}

	public void createRunningSnapshot(final String engineName,
			final List<ServiceAddress> engineServices,
			final Set<Integer> forwardingPorts)
			throws VirtualizedEngineServiceException, PortUsageException,
			PortRedirectException, InterruptedException {

		if (engineName == null || engineName.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"The name of the engine to import must not be null or empty");
		}
		if (engineServices == null || engineServices.isEmpty()) {
			throw new IllegalArgumentException(
					"The list of services to verify if a vm has been started must not be null or empty");
		}

		if (isActive()) {
			throw new IllegalStateException("Can't create a running snapshot "
					+ "if the VM is already active. The VM should be "
					+ "poweredOff!");
		}

		log.debug("Create running-state snapshot");

		try {
			applyPortForwarding(forwardingPorts);

			// start the VM for the first time
			// TODO load from config ?
			this.start(false);

			final int secondsToWait = config.getValueAsInteger(
					"virtualisation.engines." + engineName + ".serviceTimeout",
					300);
			ServiceValidator sv = new ServiceValidator();
			boolean ready = sv.isEngineReady(engineServices, secondsToWait);

			if (!ready) {
				log.warn("engine services not found withing " + secondsToWait
						+ "s");
				// timeout in CountDownLatch
				throw new VirtualizedEngineServiceException(
						"The required services for the engine were "
								+ "not available within " + secondsToWait
								+ "s after starting the vm. If using a debian/"
								+ "ubuntu system, make sure to delete the "
								+ "'/etc/udev/rules.d/70-persistent-net.rules'"
								+ "file before exporting the appliance. "
								+ "If this error occurs "
								+ "repeatedly, please import the vm manually"
								+ " with a valid snapshot in 'Running' state.");
			}

			ready = sv.isBetsyServerReady(15000);
			if (!ready) {
				log.warn("betsy server not found withing 15s");
				throw new VirtualizedEngineServiceException(
						"The required betsy server was "
								+ "not available within 15s "
								+ "after having found all other services. ");
			}

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

			// take a snapshot of the started VM
			String snapName = VirtualEngine.VIRTUAL_NAME_PREFIX + engineName
					+ "_import-snapshot";
			String snapDesc = "Machine is in 'saved' state. Snapshot created during import on "
					+ sdf.format(date);

			this.takeSnapshot(snapName, snapDesc);

			// stops the VM again and unlocks session
			this.stop();
		} catch (MalformedURLException exception) {
			throw new VirtualizedEngineServiceException(
					"Could not verify engine "
							+ "servies availability. One address is invalid: ",
					exception);
		} catch (InterruptedException exception) {
			throw exception;
		} finally {
			// stop if vm is still running
			if (this.isRunning()) {
				log.warn("VM was still running, stop now");
				this.stop();
			}
		}
	}
}

package betsy.bpel.virtual.host;

public interface VirtualMachineLifecycle {

    /**
     * start virtual machine
     */
    void start();

    /**
     * stop virtual machine
     */
    void stop();

    /**
     * Not aborted, powered off or saved.
     *
     * @return false if aborted, off or saved; true otherwise
     */
    boolean isActive();

    void kill();

    /**
     * reset to the latest snapshot
     */
    void restore();

}

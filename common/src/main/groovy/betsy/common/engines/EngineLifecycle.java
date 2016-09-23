package betsy.common.engines;

public interface EngineLifecycle {

    /**
     * Installs the engine.
     */
    void install();

    /**
     * Uninstall the engine.
     */
    void uninstall();

    /**
     * Is the engine currently installed?
     *
     * @return true if it is installed, false otherwise
     */
    boolean isInstalled();

    /**
     * Start the engine and wait until it started.
     */
    void startup();

    /**
     * Stop the engine immediately.
     */
    void shutdown();

    /**
     * Determines if the engine is running at the moment.
     *
     * @return true if is running, false otherwise
     */
    boolean isRunning();

}


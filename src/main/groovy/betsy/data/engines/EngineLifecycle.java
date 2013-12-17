package betsy.data.engines;

public interface EngineLifecycle {

    /**
     * Installs the engine.
     */
    void install();

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

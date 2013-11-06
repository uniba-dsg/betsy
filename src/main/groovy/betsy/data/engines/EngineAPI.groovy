package betsy.data.engines

import betsy.data.BetsyProcess;

interface EngineAPI {

    /**
     * The name of the engine.
     *
     * @return the unique name of the engine
     */
    String getName()

    /**
     * Setup folder structures for installation.
     */
    void prepare()

    /**
     * Installs the engine.
     */
    void install()

    /**
     * Start the engine and wait until it started.
     */
    void startup()

    /**
     * Stop the engine immediately.
     */
    void shutdown()

    /**
     * Checks whether the engine is currently running and throws an exception when it does
     */
    void failIfRunning()

    /**
     * Deploy the given <code>process</code> to the current engine.
     * Deployment is always synchronous.
     *
     * @param process to be deployed
     */
    void deploy(BetsyProcess process)

    /**
     * Build archives required for deployment.
     *
     * @param process the process for which the archives are being built
     */
    void buildArchives(BetsyProcess process)

    /**
     * Gets endpoint url of requested endpoint url. This url is used for testing the process later on.
     *
     * @param process the process
     * @return the url of the endpoint
     */
    String getEndpointUrl(BetsyProcess process)

    /**
     * Store logs used for a specific process. This is required for analysis in case of error.
     *
     * @param process the process for which to store the logs.
     */
    void storeLogs(BetsyProcess process)


}

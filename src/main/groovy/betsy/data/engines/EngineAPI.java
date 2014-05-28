package betsy.data.engines;

import betsy.data.BetsyProcess;

public interface EngineAPI extends EngineLifecycle {
    /**
     * The name of the engine.
     *
     * @return the unique name of the engine
     */
    public abstract String getName();

    /**
     * Deploy the given <code>process</code> to the current engine.
     * Deployment is always synchronous.
     *
     * @param process to be deployed
     */
    public abstract void deploy(BetsyProcess process);

    /**
     * Build archives required for deployment.
     *
     * @param process the process for which the archives are being built
     */
    public abstract void buildArchives(BetsyProcess process);

    /**
     * Gets endpoint url of requested endpoint url. This url is used for testing the process later on.
     *
     * @param process the process
     * @return the url of the endpoint
     */
    public abstract String getEndpointUrl(BetsyProcess process);

    /**
     * Store logs used for a specific process. This is required for analysis in case of error.
     *
     * @param process the process for which to store the logs.
     */
    public abstract void storeLogs(BetsyProcess process);
}

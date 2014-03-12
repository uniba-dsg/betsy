package betsy.data.engines

import betsy.data.BPMNProcess

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran
 * Date: 12.03.14
 * Time: 11:11
 */
interface BPMNEngineAPI extends EngineLifecycle{
    /**
     * The name of the engine.
     *
     * @return the unique name of the engine
     */
    String getName()

    /**
     * Deploy the given <code>process</code> to the current engine.
     * Deployment is always synchronous.
     *
     * @param process to be deployed
     */
    void deploy(BPMNProcess process)

    /**
     * Build archives required for deployment.
     *
     * @param process the process for which the archives are being built
     */
    void buildArchives(BPMNProcess process)

    /**
     * Gets endpoint url of requested endpoint url. This url is used for testing the process later on.
     *
     * @param process the process
     * @return the url of the endpoint
     */
    String getEndpointUrl(BPMNProcess process)

    /**
     * Store logs used for a specific process. This is required for analysis in case of error.
     *
     * @param process the process for which to store the logs.
     */
    void storeLogs(BPMNProcess process)

    //TODO find the right place for it
    void buildTest(BPMNProcess process)
    void testProcess(BPMNProcess process)
}

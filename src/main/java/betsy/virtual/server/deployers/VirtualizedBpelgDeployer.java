package betsy.virtual.server.deployers;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Deployer for the virtualized BPEL-g engine.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedBpelgDeployer implements VirtualizedEngineDeployer {

    private static final Logger log = Logger
            .getLogger(VirtualizedBpelgDeployer.class);

    @Override
    public String getName() {
        return "bpelg_v";
    }

    @Override
    public void onPostDeployment(DeployOperation container)
            throws DeployException {
        log.info("waiting for the bpel-g_v deployment process to fire");

        long start = -System.currentTimeMillis();
        int deployTimeout = container.getDeployTimeout();

        while (!isDeployedFileAvailable(container)
                && (System.currentTimeMillis() + start < deployTimeout)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        if (!isDeployedFileAvailable(container)) {
            // timed out :/
            throw new DeployException("Process could not be deployed within "
                    + deployTimeout + "seconds. The operation timed out.");
        }

        // process is deployed, now wait for verification in logfile
        File catalinaLog = new File(container.getEngineLogfileDir(),
                "catalina.out");
        String successMessage = "Deployment successful";
        String errorMessage = "Deployment failed";
        DeploymentLogVerificator dlv = new DeploymentLogVerificator(
                catalinaLog, successMessage, errorMessage);

        if (catalinaLog.isFile()) {
            dlv.waitForDeploymentCompletition(container.getDeployTimeout());
        } else {
            log.warn("Catalina.out not found, wait 1s for deployment");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private boolean isDeployedFileAvailable(DeployOperation container) {
        File file = new File(container.getDeploymentDir() + "/work/ae_temp_"
                + container.getBpelFileNameWithoutExtension()
                + "_zip/deploy.xml");
        return file.isFile();
    }

    @Override
    public void deploy(DeployOperation container) throws DeployException {
        try {
            File file = new File(container.getDeploymentDir(), container
                    .getFileMessage().getFilename());
            FileUtils.writeByteArrayToFile(file, container.getFileMessage()
                    .getData());
        } catch (IOException exception) {
            throw new DeployException("Couldn't write the container data to "
                    + "the local disk:", exception);
        }
    }

}

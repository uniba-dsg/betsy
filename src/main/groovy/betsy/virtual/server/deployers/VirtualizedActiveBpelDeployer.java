package betsy.virtual.server.deployers;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Deployer for the virtualized Active-BPEL engine.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedActiveBpelDeployer implements VirtualizedEngineDeployer {

    private static final Logger log = Logger.getLogger(VirtualizedActiveBpelDeployer.class);

    @Override
    public String getName() {
        return "active_bpel_v";
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

    @Override
    public void onPostDeployment(DeployOperation container)
            throws DeployException {
        long start = -System.currentTimeMillis();
        int deployTimeout = container.getDeployTimeout();

        log.info("waiting for the active_bpel_v deployment process to fire for "
                + deployTimeout + " seconds");

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

        log.info("Deploy catalog found");

        // process is deployed, now wait for verification in logfile
        File catalinaLog = new File(container.getEngineLogfileDir(),
                "aeDeployment.log");
        String successMessage = "["
                + container.getBpelFileNameWithoutExtension() + ".pdd]";
        DeploymentLogVerificator dlv = new DeploymentLogVerificator(
                catalinaLog, successMessage);

        if (catalinaLog.isFile()) {
            dlv.waitForDeploymentCompletition(container.getDeployTimeout());
        } else {
            log.warn("aeDeployment.log not found, wait 1s for deployment");
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
                + "_bpr/META-INF/catalog.xml");
        return file.isFile();
    }
}

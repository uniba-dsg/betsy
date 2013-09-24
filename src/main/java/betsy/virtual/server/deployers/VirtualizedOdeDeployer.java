package betsy.virtual.server.deployers;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Deployer for the virtualized Ode engine.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedOdeDeployer implements VirtualizedEngineDeployer {

    private static final Logger log = Logger
            .getLogger(VirtualizedOdeDeployer.class);

    @Override
    public String getName() {
        return "ode_v";
    }

    @Override
    public void deploy(DeployOperation container) throws DeployException {
        try {
            unzipContainer(container);
        } catch (IOException exception) {
            throw new DeployException("Couldn't write the container data to "
                    + "the local disk:", exception);
        }
    }

    private void unzipContainer(DeployOperation container) throws IOException {
        ZipEntry entry;
        File deployFolder = new File(container.getDeploymentDir(), container
                .getFileMessage().getFilename().replace(".zip", ""));

        try (ZipInputStream zipStream = new ZipInputStream(
                new ByteArrayInputStream(container.getFileMessage().getData()))) {
            boolean createdParent = deployFolder.mkdirs();
            log.debug("Created parent dirs? " + createdParent);
            if (createdParent) {
                setPosixPermissions(deployFolder.toPath());
            }

            // unzip the zip file stored in the deployContainer
            while ((entry = zipStream.getNextEntry()) != null) {
                // prepare
                String entryName = entry.getName();
                File outputFile = new File(deployFolder, entryName);

                // write content
                FileOutputStream out = new FileOutputStream(outputFile);
                byte[] buf = new byte[4096];
                int bytesRead;
                while ((bytesRead = zipStream.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                }
                out.close();
                zipStream.closeEntry();

                // make file at least readable for every user
                setPosixPermissions(outputFile.toPath());
            }
        }
    }

    private void setPosixPermissions(final Path path) {
        try {
            // using PosixFilePermission to set file permissions 777
            Set<PosixFilePermission> permissions = new HashSet<>();
            // set owner
            permissions.add(PosixFilePermission.OWNER_READ);
            permissions.add(PosixFilePermission.OWNER_WRITE);
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
            // set group
            permissions.add(PosixFilePermission.GROUP_READ);
            permissions.add(PosixFilePermission.GROUP_WRITE);
            permissions.add(PosixFilePermission.GROUP_EXECUTE);
            // set other
            permissions.add(PosixFilePermission.OTHERS_READ);
            permissions.add(PosixFilePermission.OTHERS_WRITE);
            permissions.add(PosixFilePermission.OTHERS_EXECUTE);

            Files.setPosixFilePermissions(path, permissions);
            log.debug("New file permissions successfully applied to '"
                    + path.toString() + "'.");
        } catch (IOException exception) {
            log.warn("Exception while setting new file permissions:", exception);
        }
    }

    @Override
    public void onPostDeployment(DeployOperation container)
            throws DeployException {
        log.info("waiting for the ode_v deployment process to fire");

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
                    + deployTimeout + "seconds. .deployed not found. The "
                    + "operation timed out.");
        }

        // process is deployed, now wait for verification in logfile
        File catalinaLog = new File(container.getEngineLogfileDir(),
                "catalina.out");
        String successMessage = "Deployment of artifact "
                + container.getBpelFileNameWithoutExtension() + " successful";
        String errorMessage = "Deployment of "
                + container.getBpelFileNameWithoutExtension() + " failed";
        DeploymentLogVerificator dlv = new DeploymentLogVerificator(
                catalinaLog, successMessage, errorMessage);

        if (catalinaLog.isFile()) {
            dlv.waitForDeploymentCompletition(container.getDeployTimeout());
        } else {
            log.warn("Catalina.out not found, skip log verification and wait "
                    + "2s for deployment");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private boolean isDeployedFileAvailable(DeployOperation container) {
        File file = new File(container.getDeploymentDir() + "/"
                + container.getBpelFileNameWithoutExtension() + ".deployed");
        return file.isFile();
    }
}

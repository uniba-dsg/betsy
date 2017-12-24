package betsy.bpel.virtual.server.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import betsy.bpel.engines.activebpel.ActiveBpelDeployer;
import betsy.bpel.engines.bpelg.BpelgDeployer;
import betsy.bpel.engines.ode.OdeDeployer;
import betsy.bpel.engines.openesb.OpenEsbCLI;
import betsy.bpel.engines.openesb.OpenEsbDeployer;
import betsy.bpel.engines.orchestra.OrchestraDeployer;
import betsy.bpel.engines.petalsesb.PetalsEsbDeployer;
import betsy.bpel.virtual.common.exceptions.ChecksumException;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.exceptions.ConnectionException;
import betsy.bpel.virtual.common.exceptions.DeployException;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployResponse;
import org.apache.log4j.Logger;

public class DeployOperation {

    private final static Logger log = Logger.getLogger(DeployOperation.class);

    public static DeployResponse deployOperation(DeployRequest request) throws DeployException, ChecksumException, ConnectionException {
        if (request.getFileMessage().isInvalid()) {
            throw new ChecksumException("Given data is invalid");
        }
        log.info("Received file is valid");

        Path pathToPackageFile = saveFileToDisk(request);

        log.info("Calling deployer for engine " + request.getEngineName());
        Path deploymentDirPath = Paths.get(request.getDeploymentDir());
        Path engineLogfileDirPath = Paths.get(request.getEngineLogfileDir());
        if ("petalsesb_v".equals(request.getEngineName())) {
            PetalsEsbDeployer deployer = new PetalsEsbDeployer(deploymentDirPath, engineLogfileDirPath, request.getDeployTimeout());
            deployer.deploy(pathToPackageFile, request.getProcessName());
        } else if ("ode_v".equals(request.getEngineName())) {
            OdeDeployer deployer = new OdeDeployer(deploymentDirPath, engineLogfileDirPath, request.getDeployTimeout());
            deployer.deploy(pathToPackageFile, request.getProcessName());
        } else if ("bpelg_v".equals(request.getEngineName())) {
            BpelgDeployer deployer = new BpelgDeployer(deploymentDirPath, engineLogfileDirPath, request.getDeployTimeout());
            deployer.deploy(pathToPackageFile, request.getProcessName() );
        } else if ("active_bpel_v".equals(request.getEngineName())) {
            ActiveBpelDeployer deployer = new ActiveBpelDeployer(deploymentDirPath, engineLogfileDirPath, request.getDeployTimeout());
            deployer.deploy(pathToPackageFile, request.getProcessName());
        } else if ("orchestra_v".equals(request.getEngineName())) {
            OrchestraDeployer deployer = new OrchestraDeployer(deploymentDirPath);
            deployer.deploy(pathToPackageFile, request.getProcessName());
        } else if ("openesb_v".equals(request.getEngineName())) {
            OpenEsbCLI cli = new OpenEsbCLI(Paths.get("/opt/openesb/glassfish"));

            OpenEsbDeployer deployer = new OpenEsbDeployer(cli);
            deployer.deploy(request.getProcessName(), pathToPackageFile, Paths.get("/opt/betsy"));
        } else {
            throw new CommunicationException("invalid engine given");
        }

        log.info("Deployment successful -> sending response");
        return new DeployResponse();
    }

    public static Path saveFileToDisk(DeployRequest operation) throws DeployException {
        // write data to temp dir first
        Path tmpDeployFile = Paths.get("tmp").resolve(operation.getFileMessage().getFilename());
        try {
            Files.write(tmpDeployFile, operation.getFileMessage().getData());
            log.info("Received file is stored to disk");
            return tmpDeployFile.toAbsolutePath();
        } catch (IOException e) {
            throw new DeployException("Couldn't write the container data to "
                    + "the local disk:", e);
        }
    }

}

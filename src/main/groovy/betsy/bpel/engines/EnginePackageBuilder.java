package betsy.bpel.engines;

import betsy.bpel.model.BetsyProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.ZipTasks;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class EnginePackageBuilder {
    public void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
        // engine independent package steps
        FileTasks.mkdirs(process.getTargetPath());
        FileTasks.mkdirs(process.getTargetBpelPath());

        log.info("Copying BPEL, WSDL and additional files to target directory");

        FileTasks.copyFileIntoFolder(process.getBpelFilePath(), process.getTargetBpelPath());
        FileTasks.replaceTokenInFile(process.getTargetBpelFilePath(), "../", "");

        for (Path wsdlPath : process.getWsdlPaths()) {
            FileTasks.copyFileIntoFolder(wsdlPath, process.getTargetBpelPath());
        }
        for (Path additionalPath : process.getAdditionalFilePaths()) {
            FileTasks.copyFileIntoFolder(additionalPath, process.getTargetBpelPath());
        }
    }

    public void bpelFolderToZipFile(BetsyProcess process) {
        FileTasks.mkdirs(process.getTargetPackagePath());
        ZipTasks.zipFolder(process.getTargetPackageFilePath(), process.getTargetBpelPath());
    }

    public void replaceEndpointTokenWithValue(final BetsyProcess process) {
        log.info("Setting WSDL endpoint to " + process.getEndpoint());
        FileTasks.replaceTokenInFile(process.getTargetBpelPath().resolve("TestInterface.wsdl"), "ENDPOINT_URL", process.getEndpoint());
    }

    public void replacePartnerTokenWithValue(BetsyProcess process) {
        log.info("Setting Partner Address to " + Configuration.get("partner.ipAndPort"));
        FileTasks.replaceTokensInFolder(process.getTargetBpelPath(), "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));
    }

    private static final Logger log = Logger.getLogger(EnginePackageBuilder.class);
}

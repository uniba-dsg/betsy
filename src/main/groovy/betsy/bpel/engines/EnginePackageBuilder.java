package betsy.bpel.engines;

import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.ZipTasks;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class EnginePackageBuilder {

    public void createFolderAndCopyProcessFilesToTarget(BPELProcess process) {
        // engine independent package steps
        FileTasks.mkdirs(process.getTargetPath());
        FileTasks.mkdirs(process.getTargetProcessPath());

        log.info("Copying BPEL, WSDL and additional files to target directory");

        FileTasks.copyFileIntoFolder(process.getProcess(), process.getTargetProcessPath());
        FileTasks.replaceTokenInFile(process.getTargetProcessFilePath(), "../", "");

        for (Path wsdlPath : process.getWsdlPaths()) {
            FileTasks.copyFileIntoFolder(wsdlPath, process.getTargetProcessPath());
        }
        for (Path additionalPath : process.getAdditionalFilePaths()) {
            FileTasks.copyFileIntoFolder(additionalPath, process.getTargetProcessPath());
        }
    }

    public void bpelFolderToZipFile(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetPackagePath());
        ZipTasks.zipFolder(process.getTargetPackageFilePath(), process.getTargetProcessPath());
    }

    public void replaceEndpointTokenWithValue(final BPELProcess process) {
        log.info("Setting WSDL endpoint to " + process.getEndpoint());
        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "ENDPOINT_URL", process.getEndpoint());
    }

    public void replacePartnerTokenWithValue(BPELProcess process) {
        log.info("Setting Partner Address to " + Configuration.get("partner.ipAndPort"));
        FileTasks.replaceTokensInFolder(process.getTargetProcessPath(), "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));
    }

    private static final Logger log = Logger.getLogger(EnginePackageBuilder.class);
}

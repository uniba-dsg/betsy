package betsy.bpel.engines

import betsy.bpel.model.BetsyProcess
import betsy.common.config.Configuration
import betsy.common.tasks.FileTasks
import betsy.common.tasks.ZipTasks
import org.apache.log4j.Logger

import java.nio.file.Path

public class EnginePackageBuilder {

    private static final Logger log = Logger.getLogger(EnginePackageBuilder)

    public void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
        // engine independent package steps
        FileTasks.mkdirs(process.targetPath)
        FileTasks.mkdirs(process.targetBpelPath)

        log.info "Copying BPEL, WSDL and additional files to target directory"

        FileTasks.copyFileIntoFolder(process.bpelFilePath, process.targetBpelPath)
        FileTasks.replaceTokenInFile(process.targetBpelFilePath, "../", "")

        for (Path wsdlPath : process.wsdlPaths) {
            FileTasks.copyFileIntoFolder(wsdlPath, process.targetBpelPath)
        }

        for (Path additionalPath : process.additionalFilePaths) {
            FileTasks.copyFileIntoFolder(additionalPath, process.targetBpelPath)
        }
    }

    public void bpelFolderToZipFile(BetsyProcess process) {
        FileTasks.mkdirs(process.targetPackagePath)
        ZipTasks.zipFolder(process.targetPackageFilePath, process.targetBpelPath)
    }

    public void replaceEndpointTokenWithValue(BetsyProcess process) {
        log.info "Setting WSDL endpoint to ${process.endpoint}"
        FileTasks.replaceTokenInFile(process.targetBpelPath.resolve("TestInterface.wsdl"), "ENDPOINT_URL", process.endpoint)
    }

    public void replacePartnerTokenWithValue(BetsyProcess process) {
        log.info "Setting Partner Address to ${Configuration.get("partner.ipAndPort")}"

        Map<String, Object> replacements = new HashMap<>();
        replacements.put("PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));
        FileTasks.replaceTokensInFolder(process.targetBpelPath, replacements)
    }

}

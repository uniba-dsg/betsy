package betsy.bpel.engines

import ant.tasks.AntUtil
import betsy.common.config.Configuration;
import betsy.bpel.model.BetsyProcess
import betsy.common.tasks.FileTasks
import org.apache.log4j.Logger

public class EnginePackageBuilder {

    private static final Logger log = Logger.getLogger(EnginePackageBuilder)

    protected final AntBuilder ant = AntUtil.builder()

    public void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
        // engine independent package steps
        FileTasks.mkdirs(process.targetPath)

        log.info "Copying BPEL, WSDL and additional files to target directory"

        ant.copy file: process.bpelFilePath, todir: process.targetBpelPath
        ant.replace(file: process.targetBpelFilePath, token: "../", value: "")

        process.wsdlPaths.each { wsdlPath ->
            ant.copy file: wsdlPath, todir: process.targetBpelPath
        }

        process.additionalFilePaths.each { additionalPath ->
            ant.copy file: additionalPath, todir: process.targetBpelPath
        }
    }

    public void bpelFolderToZipFile(BetsyProcess process) {
        FileTasks.mkdirs(process.targetPackagePath)
        ant.zip file: process.targetPackageFilePath, basedir: process.targetBpelPath
    }

    public void replaceEndpointTokenWithValue(BetsyProcess process) {
        log.info "Setting WSDL endpoint to ${process.endpoint}"
        FileTasks.replaceTokenInFile(process.targetBpelPath.resolve("TestInterface.wsdl"), "ENDPOINT_URL", process.endpoint)
    }

    public void replacePartnerTokenWithValue(BetsyProcess process) {
        log.info "Setting Partner Address to ${Configuration.get("partner.ipAndPort")}"

        FileTasks.replaceTokenInFile(process.targetBpelPath, "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"))
    }

}

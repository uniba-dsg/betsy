package betsy.data.engines

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks
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
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "ENDPOINT_URL", value: process.endpoint)
    }

    public void replacePartnerTokenWithValue(BetsyProcess process) {
        log.info "Setting Partner Address to ${Configuration.get("partner.ipAndPort")}"
        ant.replace(dir: process.targetBpelPath, token: "PARTNER_IP_AND_PORT", value: Configuration.get("partner.ipAndPort"))
    }

}

package betsy.data.engines

import betsy.Configuration
import betsy.data.BetsyProcess

public class EnginePackageBuilder {

    protected final AntBuilder ant = new AntBuilder()
    protected final Configuration config = Configuration.getInstance();

    public void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
        // engine independent package steps
        ant.mkdir dir: process.targetPath

        ant.echo message: "Copying files for ${process} for engine ${process.engine.name}"

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
        ant.mkdir dir: process.targetPackagePath
        ant.zip file: process.targetPackageFilePath, basedir: process.targetBpelPath
    }

    public void replaceEndpointTokenWithValue(BetsyProcess process) {
        ant.echo message: "Setting Endpoint of wsdl IF for $process on ${process.engine.name} to ${process.endpoint}"
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "ENDPOINT_URL", value: process.endpoint)
    }

    public void replacePartnerTokenWithValue(BetsyProcess process) {
        ant.echo message: "Setting Partner Address of for $process on ${process.engine.name} to ${config.getValueAsString('PARTNER_IP_AND_PORT')}"
        ant.replace(dir: process.targetBpelPath, token: "PARTNER_IP_AND_PORT", value: config.getValueAsString('PARTNER_IP_AND_PORT'))
    }

}

package betsy.executables.generator

import betsy.Configuration
import betsy.data.Process
import betsy.data.Engine


class PackageBuilder {

    AntBuilder ant = new AntBuilder()

    Process process

    void buildPackage() {
        Engine engine = process.engine

        // engine independent package steps
        ant.mkdir dir: process.targetPath

        ant.echo message: "Copying files for $process for engine ${engine}"

        ant.copy file: process.bpelFilePath, todir: process.targetBpelPath
        ant.replace(file: process.targetBpelFilePath, token: "../", value: "")

        process.wsdlPaths.each { wsdlPath ->
            ant.copy file: wsdlPath, todir: process.targetBpelPath
        }

        process.additionalFilePaths.each {additionalPath ->
            ant.copy file: additionalPath, todir: process.targetBpelPath
        }

        // engine specific steps
        ant.echo message: "Building deployment descriptor for $process on ${engine}"
        engine.buildDeploymentDescriptor(process)

        ant.echo message: "Apply ${engine.name} transformations for $process"
        engine.transform(process)

        ant.echo message: "Setting Endpoint of wsdl IF for $process on ${engine} to ${process.endpoint}"
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "ENDPOINT_URL", value: process.endpoint)
        ant.replace(dir: process.targetBpelPath, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)

        ant.mkdir dir: process.targetPackagePath
        ant.zip file: process.targetPackageFilePath, basedir: process.targetBpelPath

        engine.buildAdditionalArchives(process)
    }
}

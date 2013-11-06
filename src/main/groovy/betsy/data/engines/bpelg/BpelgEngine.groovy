package betsy.data.engines.bpelg

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.data.engines.Util
import betsy.data.engines.tomcat.Tomcat

class BpelgEngine extends LocalEngine {

    @Override
    String getName() {
        "bpelg"
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/bpr"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/bpel-g/services/${process.bpelFileNameWithoutExtension}TestInterfaceService"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath)
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    void install() {
        new BpelgInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        new BpelgDeployer(processName: process.bpelFileNameWithoutExtension,
                packageFilePath: process.targetPackageFilePath,
                deploymentDirPath: getDeploymentDir(),
                logFilePath: "${tomcat.tomcatDir}/logs/bpelg.log"
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // deployment descriptor
        ant.xslt(in: process.bpelFilePath, out: "${process.targetBpelPath}/deploy.xml", style: "${getXsltPath()}/bpelg_bpel_to_deploy_xml.xsl")

        // remove unimplemented methods
        Util.computeMatchingPattern(process).each { pattern ->
            ant.copy(file: "${process.targetBpelPath}/TestInterface.wsdl", tofile: "${process.targetTmpPath}/TestInterface.wsdl.before_removing_${pattern}")
            ant.xslt(in: "${process.targetTmpPath}/TestInterface.wsdl.before_removing_${pattern}", out: "${process.targetBpelPath}/TestInterface.wsdl", style: "$xsltPath/bpelg_prepare_wsdl.xsl", force: "yes") {
                param(name: "deletePattern", expression: pattern)
            }
        }

        // uniquify service name
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")
        ant.replace(file: "${process.targetBpelPath}/deploy.xml", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

}

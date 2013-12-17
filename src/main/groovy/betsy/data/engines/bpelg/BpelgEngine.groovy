package betsy.data.engines.bpelg

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.data.engines.Util
import betsy.data.engines.tomcat.Tomcat
import betsy.tasks.FileTasks

import java.nio.file.Path

class BpelgEngine extends LocalEngine {

    @Override
    String getName() {
        "bpelg"
    }

    Path getDeploymentDir() {
        tomcat.tomcatDir.resolve("bpr")
    }

    @Override
    void storeLogs(BetsyProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: tomcat.tomcatLogsDir)
        }
    }

    @Override
    boolean isRunning() {
        try {
            tomcat.checkIfIsRunning()
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/bpel-g/services/${process.name}TestInterfaceService"
    }

    Tomcat getTomcat() {
        new Tomcat(engineDir: serverPath)
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
        new BpelgInstaller(serverDir: serverPath).install()
    }

    @Override
    void deploy(BetsyProcess process) {
        new BpelgDeployer(processName: process.name,
                packageFilePath: process.targetPackageFilePath,
                deploymentDirPath: getDeploymentDir(),
                logFilePath: tomcat.tomcatLogsDir.resolve("bpelg.log")
        ).deploy()
    }

    @Override
    void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // deployment descriptor
        ant.xslt(in: process.bpelFilePath,
                out: process.targetBpelPath.resolve("deploy.xml"),
                style: xsltPath.resolve("bpelg_bpel_to_deploy_xml.xsl"))

        // remove unimplemented methods
        Util.computeMatchingPattern(process).each { pattern ->
            ant.copy(file: process.targetBpelPath.resolve("TestInterface.wsdl"),
                    tofile: process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"))
            ant.xslt(in: process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"),
                    out: process.targetBpelPath.resolve("TestInterface.wsdl"),
                    style: xsltPath.resolve("bpelg_prepare_wsdl.xsl"), force: "yes") {
                param(name: "deletePattern", expression: pattern)
            }
        }

        // uniquify service name
        ant.replace(file: process.targetBpelPath.resolve("TestInterface.wsdl"),
                token: "TestInterfaceService",
                value: "${process.name}TestInterfaceService")
        ant.replace(file: process.targetBpelPath.resolve("deploy.xml"),
                token: "TestInterfaceService",
                value: "${process.name}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

}

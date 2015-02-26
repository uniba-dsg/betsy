package betsy.bpel.engines.bpelg

import betsy.bpel.model.BPELProcess
import betsy.bpel.engines.AbstractLocalEngine
import betsy.common.engines.Util
import betsy.bpel.engines.tomcat.Tomcat
import betsy.bpel.model.steps.WsdlOperation
import betsy.common.tasks.FileTasks

import java.nio.file.Path

class BpelgEngine extends AbstractLocalEngine {

    @Override
    String getName() {
        "bpelg"
    }

    Path getDeploymentDir() {
        tomcat.tomcatDir.resolve("bpr")
    }

    @Override
    void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: tomcat.tomcatLogsDir)
        }
    }

    @Override
    boolean isRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    String getEndpointUrl(BPELProcess process) {
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
    void deploy(BPELProcess process) {
        new BpelgDeployer(processName: process.name,
                packageFilePath: process.targetPackageFilePath,
                deploymentDirPath: getDeploymentDir(),
                logFilePath: tomcat.tomcatLogsDir.resolve("bpelg.log")
        ).deploy()
    }

    @Override
    void buildArchives(BPELProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // deployment descriptor
        ant.xslt(in: process.process,
                out: process.targetProcessPath.resolve("deploy.xml"),
                style: xsltPath.resolve("bpelg_bpel_to_deploy_xml.xsl"))

        // remove unimplemented methods
        computeMatchingPattern(process).each { pattern ->
            ant.copy(file: process.targetProcessPath.resolve("TestInterface.wsdl"),
                    tofile: process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"))
            ant.xslt(in: process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"),
                    out: process.targetProcessPath.resolve("TestInterface.wsdl"),
                    style: xsltPath.resolve("bpelg_prepare_wsdl.xsl"), force: "yes") {
                param(name: "deletePattern", expression: pattern)
            }
        }

        // uniquify service name
        ant.replace(file: process.targetProcessPath.resolve("TestInterface.wsdl"),
                token: "TestInterfaceService",
                value: "${process.name}TestInterfaceService")
        ant.replace(file: process.targetProcessPath.resolve("deploy.xml"),
                token: "TestInterfaceService",
                value: "${process.name}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

    public static String[] computeMatchingPattern(BPELProcess process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = process.process.toFile().getText()
        String canonicalText = Util.canonicalizeXML(text)

        def operations = [WsdlOperation.SYNC_STRING, WsdlOperation.SYNC, WsdlOperation.ASYNC]
        def implementedOperations = []

        canonicalText.eachLine { line ->
            operations.each { operation ->
                if (line.contains("operation=\"${operation.name}\"") && !line.contains("invoke")) {
                    implementedOperations << operation;
                }
            }
        }

        def unimplementedOperations = operations - implementedOperations
        unimplementedOperations.collect{it.name}
    }

}

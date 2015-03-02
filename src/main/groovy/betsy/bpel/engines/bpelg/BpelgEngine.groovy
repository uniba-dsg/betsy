package betsy.bpel.engines.bpelg

import betsy.bpel.engines.AbstractLocalEngine
import betsy.bpel.model.BPELProcess
import betsy.bpel.model.steps.WsdlOperation
import betsy.common.engines.Util
import betsy.common.engines.tomcat.Tomcat
import betsy.common.tasks.FileTasks
import betsy.common.tasks.XSLTTasks

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
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder(getTomcat().tomcatLogsDir, process.getTargetLogsPath());
    }

    @Override
    boolean isRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    String getEndpointUrl(BPELProcess process) {
        "${tomcat.tomcatUrl}/bpel-g/services/${process.name}TestInterfaceService"
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
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
        new BpelgInstaller(serverPath, "bpel-g-5.3.war").install()
    }

    @Override
    void deploy(BPELProcess process) {
        new BpelgDeployer(getDeploymentDir(), tomcat.tomcatLogsDir.resolve("bpelg.log")).deploy(process.targetPackageFilePath, process.name);
    }

    @Override
    void buildArchives(BPELProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // deployment descriptor
        XSLTTasks.transform(xsltPath.resolve("bpelg_bpel_to_deploy_xml.xsl"), process.process, process.targetProcessPath.resolve("deploy.xml"))

        // remove unimplemented methods
        computeMatchingPattern(process).each { pattern ->
            FileTasks.copyFileContentsToNewFile(process.targetProcessPath.resolve("TestInterface.wsdl"), process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"))
            AntUtil.builder().xslt(in: process.targetTmpPath.resolve("TestInterface.wsdl.before_removing_${pattern}"),
                    out: process.targetProcessPath.resolve("TestInterface.wsdl"),
                    style: xsltPath.resolve("bpelg_prepare_wsdl.xsl"), force: "yes") {
                param(name: "deletePattern", expression: pattern)
            }
        }

        // uniquify service name
        FileTasks.replaceTokenInFile(process.targetProcessPath.resolve("TestInterface.wsdl"),
                "TestInterfaceService",
                "${process.name}TestInterfaceService");
        FileTasks.replaceTokenInFile(process.targetProcessPath.resolve("deploy.xml"),
                "TestInterfaceService",
                "${process.name}TestInterfaceService");

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

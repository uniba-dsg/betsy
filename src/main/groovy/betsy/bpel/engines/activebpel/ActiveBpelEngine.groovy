package betsy.bpel.engines.activebpel

import betsy.bpel.engines.AbstractLocalEngine
import betsy.bpel.model.BPELProcess
import betsy.common.engines.tomcat.Tomcat
import betsy.common.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path
import java.nio.file.Paths

/*
* Currently using in-memory mode for the engine
 */

class ActiveBpelEngine extends AbstractLocalEngine {

    private static final Logger log = Logger.getLogger(ActiveBpelEngine.class)

    @Override
    String getName() {
        return "active-bpel"
    }

    @Override
    String getEndpointUrl(BPELProcess process) {
        "${tomcat.tomcatUrl}/active-bpel/services/${process.name}TestInterfaceService"
    }

    public Tomcat getTomcat() {
        return Tomcat.v5(getServerPath());
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
        ant.copy(file: getAeDeploymentLog(), todir: process.targetLogsPath)
    }

    private static Path getAeDeploymentLog() {
        String homeDir = System.getProperty("user.home");
        homeDir = homeDir.endsWith(File.separator) ?: homeDir + File.separator;

        Paths.get("$homeDir/AeBpelEngine/deployment-logs/aeDeployment.log")
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
    boolean isRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    void install() {
        new ActiveBpelInstaller().install()
    }

    @Override
    void deploy(BPELProcess process) {
        new ActiveBpelDeployer(deploymentDirPath: getDeploymentDir(),
                logFilePath: getAeDeploymentLog(),
                processName: process.name,
                packageFilePath: process.getTargetPackageFilePath("bpr")).deploy()
    }

    public void buildArchives(BPELProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // create deployment descriptor
        Path metaDir = process.getTargetProcessPath().resolve("META-INF")
        FileTasks.createFile(metaDir.resolve("MANIFEST.MF"), "Manifest-Version: 1.0");
        ant.xslt(in: process.process, out: metaDir.resolve("${process.name}.pdd"), style: xsltPath.resolve("active-bpel_to_deploy_xml.xsl"))
        ant.xslt(in: process.process, out: metaDir.resolve("catalog.xml"), style: xsltPath.resolve("active-bpel_to_catalog.xsl"))

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        // create bpr file
        FileTasks.move(process.targetPackageFilePath, process.getTargetPackageFilePath("bpr"))
    }

}

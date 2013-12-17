package betsy.data.engines.activebpel

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.data.engines.tomcat.Tomcat
import betsy.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path
import java.nio.file.Paths

/*
* Currently using in-memory mode for the engine
 */

class ActiveBpelEngine extends LocalEngine {

    private static final Logger log = Logger.getLogger(ActiveBpelEngine.class)

    @Override
    String getName() {
        return "active-bpel"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/active-bpel/services/${process.name}TestInterfaceService"
    }

    Tomcat getTomcat() {
        new Tomcat(engineDir: serverPath, tomcatName: "apache-tomcat-5.5.36")
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
        try {
            tomcat.checkIfIsRunning()
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }

    @Override
    void install() {
        new ActiveBpelInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        new ActiveBpelDeployer(deploymentDirPath: getDeploymentDir(),
                logFilePath: getAeDeploymentLog(),
                processName: process.name,
                packageFilePath: process.getTargetPackageFilePath("bpr")).deploy()
    }

    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // create deployment descriptor
        Path metaDir = process.targetBpelPath.resolve("META-INF")
        ant.echo file: metaDir.resolve("MANIFEST.MF"), message: "Manifest-Version: 1.0"
        ant.xslt(in: process.bpelFilePath, out: "$metaDir/${process.name}.pdd", style: xsltPath.resolve("active-bpel_to_deploy_xml.xsl"))
        ant.xslt(in: process.bpelFilePath, out: metaDir.resolve("catalog.xml"), style: xsltPath.resolve("active-bpel_to_catalog.xsl"))

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        // create bpr file
        ant.move(file: process.targetPackageFilePath, toFile: process.getTargetPackageFilePath("bpr"))
    }

}

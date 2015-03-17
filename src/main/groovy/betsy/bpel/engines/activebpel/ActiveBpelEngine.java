package betsy.bpel.engines.activebpel;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ActiveBpelEngine extends AbstractLocalBPELEngine {

    @Override
    public String getName() {
        return "active-bpel";
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return getTomcat().getTomcatUrl() + "/active-bpel/services/" + process.getName() + "TestInterfaceService";
    }

    public Tomcat getTomcat() {
        return Tomcat.v5(getServerPath());
    }

    public Path getDeploymentDir() {
        return getTomcat().getTomcatDir().resolve("bpr");
    }

    @Override
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder(getTomcat().getTomcatLogsDir(), process.getTargetLogsPath());
        FileTasks.copyFileIntoFolder(getAeDeploymentLog(), process.getTargetLogsPath());
    }

    private static Path getAeDeploymentLog() {
        String homeDir = System.getProperty("user.home");

        // ensure that homeDir ends with File.separator
        homeDir = homeDir.endsWith(File.separator) ? homeDir : homeDir + File.separator;

        Path homeDirPath = Paths.get(homeDir);
        return homeDirPath.resolve("AeBpelEngine/deployment-logs/aeDeployment.log");
    }

    @Override
    public void startup() {
        getTomcat().startup();
    }

    @Override
    public void shutdown() {
        getTomcat().shutdown();
    }

    @Override
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

    @Override
    public void install() {
        new ActiveBpelInstaller().install();
    }

    @Override
    public void deploy(BPELProcess process) {
        new ActiveBpelDeployer(getDeploymentDir(), getAeDeploymentLog()).deploy(process.getTargetPackageFilePath("bpr"), process.getName());
    }

    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // create deployment descriptor
        Path metaDir = process.getTargetProcessPath().resolve("META-INF");
        FileTasks.createFile(metaDir.resolve("MANIFEST.MF"), "Manifest-Version: 1.0");
        XSLTTasks.transform(getXsltPath().resolve("active-bpel_to_deploy_xml.xsl"), process.getProcess(), metaDir.resolve(process.getName() + ".pdd"));
        XSLTTasks.transform(getXsltPath().resolve("active-bpel_to_catalog.xsl"), process.getProcess(), metaDir.resolve("catalog.xml"));

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        // create bpr file
        FileTasks.move(process.getTargetPackageFilePath(), process.getTargetPackageFilePath("bpr"));
    }

    private static
    final Logger log = Logger.getLogger(betsy.bpel.engines.activebpel.ActiveBpelEngine.class);
}

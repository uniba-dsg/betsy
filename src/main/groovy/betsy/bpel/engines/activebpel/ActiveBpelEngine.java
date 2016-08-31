package betsy.bpel.engines.activebpel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

public class ActiveBpelEngine extends AbstractLocalBPELEngine {

    private static final Logger log = Logger.getLogger(ActiveBpelEngine.class);

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "activebpel", "5.0.2", LocalDate.of(2008, 5, 9), "GPL-2.0+");
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/active-bpel");
    }

    @Override
    public String getEndpointUrl(String name) {
        return getTomcat().getTomcatUrl() + "/active-bpel/services/" + name + "TestInterfaceService";
    }

    public Tomcat getTomcat() {
        return Tomcat.v5(getServerPath());
    }

    public Path getDeploymentDir() {
        return getTomcat().getTomcatDir().resolve("bpr");
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getTomcat().getTomcatLogsDir()));
        result.add(getAeDeploymentLog());

        return result;
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
        Tomcat tomcat = getTomcat();
        tomcat.setJavaVersion(Tomcat.JavaVersion.V7);
        tomcat.startup();
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
        new ActiveBpelInstaller(getServerPath()).install();
    }

    @Override
    public void deploy(String name, Path path) {
        new ActiveBpelDeployer(getDeploymentDir(), getAeDeploymentLog()).
                deploy(path, name);
    }

    @Override
    public boolean isDeployed(QName process) {
        return new ActiveBpelDeployer(getDeploymentDir(), getAeDeploymentLog())
                .isDeployed(process);
    }

    @Override
    public void undeploy(QName process) {
        new ActiveBpelDeployer(getDeploymentDir(), getAeDeploymentLog())
                .undeploy(process);
    }

    public Path buildArchives(BPELProcess process) {
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

        return process.getTargetPackageFilePath("bpr");
    }

}

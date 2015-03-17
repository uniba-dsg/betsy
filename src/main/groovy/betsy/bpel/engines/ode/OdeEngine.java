package betsy.bpel.engines.ode;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;

import java.nio.file.Path;

public class OdeEngine extends AbstractLocalBPELEngine {

    public static final String TEST_INTERFACE_SERVICE = "TestInterfaceService";

    @Override
    public String getName() {
        return "ode";
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return getTomcat().getTomcatUrl() + "/ode/processes/" + process.getName() + "TestInterface";
    }

    public Path getDeploymentDir() {
        return getTomcat().getTomcatDir().resolve("webapps/ode/WEB-INF/processes");
    }

    public Tomcat getTomcat() {
        return Tomcat.v7(getServerPath());
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
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder( getTomcat().getTomcatLogsDir(), process.getTargetLogsPath());
    }

    @Override
    public void install() {
        new OdeInstaller(getServerPath(), "apache-ode-war-1.3.5.zip").install();
    }

    @Override
    public void deploy(BPELProcess process) {
        OdeDeployer deployer = new OdeDeployer(getDeploymentDir(), getTomcat().getTomcatLogsDir().resolve("ode.log"), 30);
        deployer.deploy(process.getTargetPackageFilePath(), process.getName());
    }

    @Override
    public void buildArchives(final BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        XSLTTasks.transform(getXsltPath().resolve("bpel_to_ode_deploy_xml.xsl"), process.getProcess(), process.getTargetProcessPath().resolve("deploy.xml"));

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);
        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("deploy.xml"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);

        getPackageBuilder().bpelFolderToZipFile(process);
    }

    @Override
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

}

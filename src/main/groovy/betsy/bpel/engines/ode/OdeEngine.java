package betsy.bpel.engines.ode;

import betsy.bpel.engines.LocalEngine;
import betsy.bpel.engines.tomcat.Tomcat;
import betsy.bpel.model.BetsyProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;

import java.nio.file.Path;

public class OdeEngine extends LocalEngine {

    public static final String TEST_INTERFACE_SERVICE = "TestInterfaceService";

    @Override
    public String getName() {
        return "ode";
    }

    @Override
    public String getEndpointUrl(final BetsyProcess process) {
        return getTomcat().getTomcatUrl() + "/ode/processes/" + process.getName() + "TestInterface";
    }

    public Path getDeploymentDir() {
        return getTomcat().getTomcatDir().resolve("webapps/ode/WEB-INF/processes");
    }

    public Tomcat getTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setEngineDir(getServerPath());
        return tomcat;
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
    public void storeLogs(BetsyProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder(process.getTargetLogsPath(), getTomcat().getTomcatLogsDir());
    }

    @Override
    public void install() {
        OdeInstaller installer = new OdeInstaller();
        installer.setServerDir(getServerPath());
        installer.install();
    }

    @Override
    public void deploy(BetsyProcess process) {
        OdeDeployer deployer = new OdeDeployer();
        deployer.setProcessName(process.getName());
        deployer.setLogFilePath(getTomcat().getTomcatLogsDir().resolve("ode.log"));
        deployer.setDeploymentDirPath(getDeploymentDir());
        deployer.setPackageFilePath(process.getTargetPackageFilePath());
        deployer.deploy();
    }

    @Override
    public void buildArchives(final BetsyProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        XSLTTasks.transform(getXsltPath().resolve("bpel_to_ode_deploy_xml.xsl"), process.getBpelFilePath(), process.getTargetBpelPath().resolve("deploy.xml"));

        FileTasks.replaceTokenInFile(process.getTargetBpelPath().resolve("TestInterface.wsdl"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);
        FileTasks.replaceTokenInFile(process.getTargetBpelPath().resolve("deploy.xml"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);

        getPackageBuilder().bpelFolderToZipFile(process);
    }

    @Override
    public boolean isRunning() {
        return getTomcat().checkIfIsRunning();
    }

}

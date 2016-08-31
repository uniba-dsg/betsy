package betsy.bpel.engines.wso2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.ClasspathHelper;

public class Wso2Engine_v3_1_0 extends AbstractLocalBPELEngine {

    public static final String TEST_INTERFACE_SERVICE = "TestInterfaceService";

    public static final String CHECK_URL = "https://localhost:9443/carbon";

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "wso2", "3.1.0", LocalDate.of(2013, 12, 6), "Apache-2.0");
    }

    @Override
    public void install() {
        FileTasks.deleteDirectory(getServerPath());
        // setup engine folder
        FileTasks.mkdirs(getServerPath());

        String fileName = getZipFileName();

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), getServerPath());

        FileTasks.createFile(getServerPath().resolve("startup.bat"), "start \"" + getName() + "\" /min startup-helper.bat");
        FileTasks.createFile(getServerPath().resolve("startup-helper.bat"), "cd " + getBinDir().toAbsolutePath() + " && call wso2server.bat");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", getServerPath().toString()));
    }

    public String getZipFileName() {
        return "wso2bps-3.1.0.zip";
    }

    @Override
    public void startup() {
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getServerPath(), "startup.bat"));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(getBinDir(), getBinDir().resolve("wso2server.sh")).values("start")); // start wso2 in background
        WaitTasks.sleep(TimeoutRepository.getTimeout("Wso2_v3_1_0.startup.sleep").getTimeoutInMs());

        Path logFile = getLogsFolder().resolve("wso2carbon.log");
        TimeoutRepository.getTimeout("Wso2_v3_1_0.startup").waitForSubstringInFile(getLogsFolder().resolve("wso2carbon.log"), "WSO2 Carbon started in ");
    }

    public Path getLogsFolder() {
        return getCarbonHome().resolve("repository").resolve("logs");
    }

    public Path getBinDir() {
        return getCarbonHome().resolve("bin");
    }

    public Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-3.1.0");
    }

    @Override
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq " + getName() + "*"));
        if(Files.exists(getBinDir())) {
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getBinDir(), getBinDir().resolve("wso2server.sh")).values("stop"));
        }
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

    @Override
    public void deploy(String name, Path path) {
        new Wso2Deployer(getDeploymentDir(), getLogsFolder())
                .deploy(path);
    }

    @Override public boolean isDeployed(QName process) {
        return new Wso2Deployer(getDeploymentDir(), getLogsFolder())
                .isDeployed(process.getLocalPart());
    }

    @Override public void undeploy(QName process) {
        new Wso2Deployer(getDeploymentDir(), getLogsFolder())
                .undeploy(process);
    }

    public Path getDeploymentDir() {
        return getCarbonHome().resolve("repository").resolve("deployment").resolve("server").resolve("bpel");
    }

    @Override
    public Path buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        XSLTTasks.transform(getXsltPath().resolve("bpel_to_ode_deploy_xml.xsl"), process.getProcess(), process.getTargetProcessPath().resolve("deploy.xml"));

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("deploy.xml"), TEST_INTERFACE_SERVICE, process.getName() + TEST_INTERFACE_SERVICE);

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);

        getPackageBuilder().bpelFolderToZipFile(process);

        return process.getTargetPackageFilePath();
    }

    @Override
    public String getEndpointUrl(String name) {
        return "http://localhost:9763" + "/services/" + name + TEST_INTERFACE_SERVICE;
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getLogsFolder()));

        return result;
    }

}

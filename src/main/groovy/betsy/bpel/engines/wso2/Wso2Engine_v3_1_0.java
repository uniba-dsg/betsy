package betsy.bpel.engines.wso2;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Wso2Engine_v3_1_0 extends AbstractLocalBPELEngine {

    public static final String TEST_INTERFACE_SERVICE = "TestInterfaceService";

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public String getName() {
        return "wso2_v3_1_0";
    }

    @Override
    public void install() {
        FileTasks.deleteDirectory(getServerPath());
        // setup engine folder
        FileTasks.mkdirs(getServerPath());

        String fileName = getZipFileName();

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), getServerPath());

        FileTasks.createFile(getServerPath().resolve("startup.bat"), "start startup-helper.bat");
        FileTasks.createFile(getServerPath().resolve("startup-helper.bat"), "TITLE wso2server\ncd " +
                getBinDir().toAbsolutePath() + " && call wso2server.bat");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getBinDir().resolve("wso2server.sh").toString()));
    }

    public String getZipFileName() {
        return "wso2bps-3.1.0.zip";
    }

    @Override
    public void startup() {
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getServerPath(), "startup.bat"));

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(getBinDir().resolve("wso2server.sh")).values("start")); // start wso2 in background
        WaitTasks.sleep(2000);
        WaitTasks.waitForSubstringInFile(120_000, 500, getLogsFolder().resolve("wso2carbon.log"), "WSO2 Carbon started in ");
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
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq wso2server"));
        // required for jenkins - may have side effects but this should not be a problem in this context
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq Administrator:*"));


        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getBinDir().resolve("wso2server.sh")).values("stop"));
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

    @Override
    public void deploy(BPELProcess process) {
        new Wso2Deployer(getDeploymentDir(), getLogsFolder()).deploy(process.getTargetPackageFilePath());
    }

    public Path getDeploymentDir() {
        return getCarbonHome().resolve("repository").resolve("deployment").resolve("server").resolve("bpel");
    }

    @Override
    public void buildArchives(BPELProcess process) {
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
    public String getEndpointUrl(final BPELProcess process) {
        return CHECK_URL + "/services/" + process.getName() + TEST_INTERFACE_SERVICE;
    }

    @Override
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for (Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getLogsFolder()));

        return result;
    }

    public static final String CHECK_URL = "http://localhost:9763";
}

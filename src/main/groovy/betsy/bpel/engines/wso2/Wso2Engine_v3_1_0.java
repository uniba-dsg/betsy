package betsy.bpel.engines.wso2;

import betsy.bpel.engines.LocalEngine;
import betsy.bpel.model.BetsyProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Wso2Engine_v3_1_0 extends LocalEngine {
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
    }

    public String getZipFileName() {
        return "wso2bps-3.1.0.zip";
    }

    @Override
    public void startup() {
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getServerPath(), "startup.bat"));
        WaitTasks.sleep(2000);
        WaitTasks.waitForSubstringInFile(60_000, 500, getLogsFolder().resolve("wso2carbon.log"), "WSO2 Carbon started in ");
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
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

    @Override
    public void deploy(BetsyProcess process) {
        FileTasks.copyFileIntoFolder(process.getTargetPackageFilePath(), getDeploymentDir());

        WaitTasks.waitForSubstringInFile(60_000, 500, getLogsFolder().resolve("wso2carbon.log"), "{org.apache.ode.bpel.engine.BpelServerImpl} -  Registered process");
        WaitTasks.sleep(20000);
    }

    public Path getDeploymentDir() {
        return getCarbonHome().resolve("repository").resolve("deployment").resolve("server").resolve("bpel");
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        XSLTTasks.transform(getXsltPath().resolve("bpel_to_ode_deploy_xml.xsl"), process.getBpelFilePath(), process.getTargetBpelPath().resolve("deploy.xml"));

        FileTasks.replaceTokenInFile(process.getTargetBpelPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        FileTasks.replaceTokenInFile(process.getTargetBpelPath().resolve("deploy.xml"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);

        getPackageBuilder().bpelFolderToZipFile(process);
    }

    @Override
    public String getEndpointUrl(final BetsyProcess process) {
        return CHECK_URL + "/services/" + process.getName() + "TestInterfaceService";
    }

    @Override
    public void storeLogs(BetsyProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());
        FileTasks.copyFilesInFolderIntoOtherFolder(getLogsFolder(), process.getTargetLogsPath());
    }

    public static final String CHECK_URL = "http://localhost:9763";
}

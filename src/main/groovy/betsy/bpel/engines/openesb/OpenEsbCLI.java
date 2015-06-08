package betsy.bpel.engines.openesb;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.util.StringUtils;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class OpenEsbCLI {

    public OpenEsbCLI(Path glassfishHome) {
        FileTasks.assertDirectory(glassfishHome);

        this.glassfishHome = glassfishHome;
    }

    public void stopDomain() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getAsAdminWindows()).values("stop-domain", "domain1"));
    }

    public void startDomain() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getAsAdminWindows()).values("start-domain", "domain1"));
    }

    private Path getAsAdminWindows() {
        return getBinFolder().resolve("asadmin.bat").toAbsolutePath();
    }

    private Path getAsAdminUnix() {
        return getBinFolder().resolve("asadmin").toAbsolutePath();
    }

    protected Path getBinFolder() {
        return glassfishHome.resolve("bin");
    }

    public void forceRedeploy(String processName, Path packageFilePath, Path tmpFolder) {
        LOGGER.info("Deploying " + processName + " from " + packageFilePath);

        Path deployCommands = tmpFolder.resolve("deploy_commands.txt");

        // QUIRK path must always be in unix style, otherwise it is not correctly deployed
        String packageFilePathUnixStyle = StringUtils.toUnixStyle(packageFilePath);

        String scriptContent = "deploy-jbi-service-assembly " + packageFilePathUnixStyle + "\n";
        scriptContent += "start-jbi-service-assembly " + processName + "Application\n";

        FileTasks.createFile(deployCommands, scriptContent);

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getAsAdminWindows()).
                values("multimode", "--file", deployCommands.toAbsolutePath().toString()));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getAsAdminUnix()).
                values("multimode", "--file", deployCommands.toAbsolutePath().toString()));
    }

    private static final Logger LOGGER = Logger.getLogger(OpenEsbCLI.class);
    private final Path glassfishHome;
}

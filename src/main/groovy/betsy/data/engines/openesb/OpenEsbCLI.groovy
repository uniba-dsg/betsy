package betsy.data.engines.openesb

import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path

class OpenEsbCLI {

    private static final Logger log = Logger.getLogger(OpenEsbCLI.class)

    Path glassfishHome

    void stopDomain() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(asAdminWindows).values("stop-domain", "domain1"))
    }

    void startDomain() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(asAdminWindows).values("start-domain", "domain1"))
    }

    private Path getAsAdminWindows() {
        getBinFolder().resolve("asadmin.bat").toAbsolutePath()
    }

    private Path getAsAdminUnix() {
        getBinFolder().resolve("asadmin").toAbsolutePath()
    }

    protected Path getBinFolder() {
        glassfishHome.resolve("bin")
    }

    void forceRedeploy(String processName, Path packageFilePath, Path tmpFolder) {
        log.info("Deploying ${processName} from ${packageFilePath}")

        Path deployCommands = tmpFolder.resolve("deploy_commands.txt")

        // QUIRK path must always be in unix style, otherwise it is not correctly deployed
        String packageFilePathUnixStyle = packageFilePath.toAbsolutePath().toString().replace("\\", "/")

        String scriptContent = "deploy-jbi-service-assembly ${packageFilePathUnixStyle}\n"
        scriptContent += "start-jbi-service-assembly ${processName}Application\n"

        FileTasks.createFile(deployCommands, scriptContent);

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(asAdminWindows).values("multimode", "--file", deployCommands.toAbsolutePath().toString()))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(asAdminUnix).values("multimode", "--file", deployCommands.toAbsolutePath().toString()))
    }

}

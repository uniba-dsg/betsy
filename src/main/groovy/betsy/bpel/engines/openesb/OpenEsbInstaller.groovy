package betsy.bpel.engines.openesb

import ant.tasks.AntUtil
import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class OpenEsbInstaller {

    final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/openesb")
    String fileName = "glassfishesb-v2.2-full-installer-windows.exe"
    Path stateXmlTemplate = ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/state.xml.template")

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(serverDir)
        FileTasks.mkdirs(serverDir)

        Path stateXmlPath = serverDir.resolve("state.xml").toAbsolutePath()
        FileTasks.copyFileContentsToNewFile(stateXmlTemplate, stateXmlPath)
        Map<String, Object> replacements = new HashMap<>();
        replacements.put("INSTALL_PATH", serverDir.toAbsolutePath());
        replacements.put("JDK_LOCATION", System.getenv()['JAVA_HOME']);
        replacements.put("HTTP_PORT", 8383);
        replacements.put("HTTPS_PORT", 8384);
        FileTasks.replaceTokensInFile(stateXmlPath, replacements)

        Path reinstallGlassFishBatPath = serverDir.resolve("reinstallGlassFish.bat")
        Files.copy(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/reinstallGlassFish.bat"),
                reinstallGlassFishBatPath)

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(
                reinstallGlassFishBatPath).values(
                Configuration.downloadsDir.resolve(fileName).toString(),
                stateXmlPath.toString())
        )

    }
}

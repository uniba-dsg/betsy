package betsy.bpel.engines.openesb;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OpenEsbInstaller {

    private final Path path;
    private final String fileName;
    private final Path stateXmlTemplate;

    public OpenEsbInstaller(Path path, String fileName, Path stateXmlTemplate) {
        this.path = Objects.requireNonNull(path);
        this.fileName = Objects.requireNonNull(fileName);
        this.stateXmlTemplate = Objects.requireNonNull(stateXmlTemplate);
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(path);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(path);
        FileTasks.mkdirs(path);

        Path stateXmlPath = path.resolve("state.xml").toAbsolutePath();
        FileTasks.copyFileContentsToNewFile(stateXmlTemplate, stateXmlPath);
        Map<String, Object> replacements = new HashMap<>();
        replacements.put("INSTALL_PATH", path.toAbsolutePath());
        replacements.put("JDK_LOCATION", System.getenv().get("JAVA_HOME"));
        replacements.put("HTTP_PORT", 8383);
        replacements.put("HTTPS_PORT", 8384);
        FileTasks.replaceTokensInFile(stateXmlPath, replacements);

        Path reinstallGlassFishBatPath = path.resolve("reinstallGlassFish.bat");
        FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/reinstallGlassFish.bat"), reinstallGlassFishBatPath);
        Path installationScript = Configuration.getDownloadsDir().resolve(fileName);
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(reinstallGlassFishBatPath).
                values(installationScript.toString(), stateXmlPath.toString()));

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", installationScript.toString()));
        ConsoleTasks.executeOnUnix(
                ConsoleTasks.CliCommand.build(path, installationScript).
                values("--silent", "--state", stateXmlPath.toString()));
    }


}

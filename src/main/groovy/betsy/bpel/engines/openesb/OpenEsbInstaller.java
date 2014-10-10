package betsy.bpel.engines.openesb;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class OpenEsbInstaller {
    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(serverDir);
        FileTasks.mkdirs(serverDir);

        Path stateXmlPath = serverDir.resolve("state.xml").toAbsolutePath();
        FileTasks.copyFileContentsToNewFile(stateXmlTemplate, stateXmlPath);
        Map<String, Object> replacements = new HashMap<String, Object>();
        replacements.put("INSTALL_PATH", serverDir.toAbsolutePath());
        replacements.put("JDK_LOCATION", System.getenv().get("JAVA_HOME"));
        replacements.put("HTTP_PORT", 8383);
        replacements.put("HTTPS_PORT", 8384);
        FileTasks.replaceTokensInFile(stateXmlPath, replacements);

        Path reinstallGlassFishBatPath = serverDir.resolve("reinstallGlassFish.bat");
        FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/reinstallGlassFish.bat"), reinstallGlassFishBatPath);

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(reinstallGlassFishBatPath).values(Configuration.getDownloadsDir().resolve(fileName).toString(), stateXmlPath.toString()));

    }

    public Path getServerDir() {
        return serverDir;
    }

    public void setServerDir(Path serverDir) {
        this.serverDir = serverDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Path getStateXmlTemplate() {
        return stateXmlTemplate;
    }

    public void setStateXmlTemplate(Path stateXmlTemplate) {
        this.stateXmlTemplate = stateXmlTemplate;
    }

    private Path serverDir = Paths.get("server/openesb");
    private String fileName = "glassfishesb-v2.2-full-installer-windows.exe";
    private Path stateXmlTemplate = ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/state.xml.template");
}

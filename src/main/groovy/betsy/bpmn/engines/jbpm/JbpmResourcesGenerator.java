package betsy.bpmn.engines.jbpm;

import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JbpmResourcesGenerator {

    public void generateProject() {
        Path resDir = destDir.resolve("src").resolve("main").resolve("resources");

        //setup directories
        FileTasks.mkdirs(destDir.resolve("src").resolve("main").resolve("java"));
        FileTasks.mkdirs(destDir.resolve("src").resolve("test").resolve("java"));
        FileTasks.mkdirs(resDir);
        FileTasks.mkdirs(resDir.resolve("META-INF"));

        //copy files
        FileTasks.copyFilesInFolderIntoOtherFolder(jbpmSrcDir.resolve("META-INF"), resDir.resolve("META-INF"));
        FileTasks.copyFileIntoFolder(jbpmSrcDir.resolve("project.imports"), destDir);

        generatePomXml();
    }

    private void generatePomXml() {
        FileTasks.copyFileIntoFolder(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm/pom.xml"), destDir);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("GROUP_ID", groupId);
        replacements.put("PROCESS_NAME", processName);
        replacements.put("_VERSION_", version);
        FileTasks.replaceTokensInFile(destDir.resolve("pom.xml"), replacements);
    }

    public Path getJbpmSrcDir() {
        return jbpmSrcDir;
    }

    public void setJbpmSrcDir(Path jbpmSrcDir) {
        this.jbpmSrcDir = jbpmSrcDir;
    }

    public Path getDestDir() {
        return destDir;
    }

    public void setDestDir(Path destDir) {
        this.destDir = destDir;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private Path jbpmSrcDir;
    private Path destDir;
    private String processName;
    private String groupId;
    private String version;
}

package betsy.bpmn.engines.camunda;

import ant.tasks.AntUtil;
import betsy.bpmn.engines.BPMNTester;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.types.FileSet;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CamundaResourcesGenerator {
    private Path srcDir;
    private Path destDir;
    private String processName;
    private String groupId;
    private String version;

    public void generateWar() {
        //directory structure
        Path pomDir = destDir.resolve("META-INF/maven").resolve(groupId).resolve(processName);
        Path classesDir = destDir.resolve("WEB-INF/classes");
        Path srcDestDir = destDir.resolve("../src").normalize().toAbsolutePath();

        //setup infrastructure
        FileTasks.mkdirs(pomDir);
        FileTasks.mkdirs(classesDir);
        FileTasks.mkdirs(srcDestDir);

        //generate pom.properties
        FileTasks.createFile(pomDir.resolve("pom.properties"), "version=" + version + "\ngroupId=" + groupId + "\nartifactId=" + processName);

        //generate pom
        generatePom(pomDir);
        generateProcessesXml(classesDir);

        BPMNTester.setupPathToToolsJarForJavacAntTask(this);

        NetworkTasks.downloadFileFromBetsyRepo("javaee-api-7.0.jar");
        NetworkTasks.downloadFileFromBetsyRepo("camunda-engine-7.0.0-Final.jar");

        // generate and compile sources
        generateServletProcessApplication(srcDestDir);
        compileServletProcessApplication(srcDestDir, classesDir);

        createWar();
    }

    private void createWar() {
        War war = new War();
        war.setDestFile(destDir.resolve(processName + ".war").toFile());
        war.setNeedxmlfile(false);

        FileSet set = new FileSet();
        set.setDir(destDir.toFile());
        war.add(set);

        war.setTaskName("war");
        war.setProject(AntUtil.builder().getProject());

        war.execute();
    }

    private void compileServletProcessApplication(Path srcDestDir, Path classesDir) {
        Javac javac = new Javac();

        org.apache.tools.ant.types.Path sourceDirPath = new org.apache.tools.ant.types.Path(AntUtil.builder().getProject());
        sourceDirPath.setLocation(srcDestDir.toFile());

        javac.setSrcdir(sourceDirPath);
        javac.setDestdir(classesDir.toFile());
        javac.setIncludeantruntime(false);
        javac.setSource("1.7");
        javac.setTarget("1.7");

        org.apache.tools.ant.types.Path path = new org.apache.tools.ant.types.Path(AntUtil.builder().getProject());
        org.apache.tools.ant.types.Path camunda = new org.apache.tools.ant.types.Path(AntUtil.builder().getProject());
        camunda.setLocation(Configuration.getDownloadsDir().resolve("camunda-engine-7.0.0-Final.jar").toFile());
        org.apache.tools.ant.types.Path jee = new org.apache.tools.ant.types.Path(AntUtil.builder().getProject());
        jee.setLocation(Configuration.getDownloadsDir().resolve("javaee-api-7.0.jar").toFile());
        path.add(camunda);
        path.add(jee);
        javac.setClasspath(path);

        javac.setTaskName("javac");
        javac.setProject(AntUtil.builder().getProject());

        javac.execute();
    }

    private void generatePom(Path pomDir) {
        FileTasks.copyFileIntoFolder(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda/pom.xml"), pomDir);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("GROUP_ID", groupId);
        replacements.put("PROCESS_NAME", processName);
        replacements.put("_VERSION_", version);
        FileTasks.replaceTokensInFile(pomDir.resolve("pom.xml"), replacements);
    }

    private void generateProcessesXml(Path classesDir) {
        Path processesDir = classesDir.resolve("META-INF");
        FileTasks.mkdirs(processesDir);
        FileTasks.copyFileIntoFolder(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda/processes.xml"), processesDir);
        FileTasks.replaceTokenInFile(processesDir.resolve("processes.xml"), "PROCESS_NAME", processName);
    }

    private void generateServletProcessApplication(Path srcDestDir) {
        Path processApplication = srcDestDir.resolve(getGroupIdAsPathValues()).resolve("ProcessTestApplication.java");
        FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/camunda/ProcessApplication.template"), processApplication);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("GROUP_ID", groupId);
        replacements.put("PROCESS_NAME", processName);
        FileTasks.replaceTokensInFile(processApplication, replacements);
    }

    private String getGroupIdAsPathValues() {
        return groupId.replaceAll("\\.", "/");
    }

    public Path getSrcDir() {
        return srcDir;
    }

    public void setSrcDir(Path srcDir) {
        this.srcDir = srcDir;
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
}

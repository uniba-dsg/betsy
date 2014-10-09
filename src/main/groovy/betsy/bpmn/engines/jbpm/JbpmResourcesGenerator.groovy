package betsy.bpmn.engines.jbpm

import ant.tasks.AntUtil
import betsy.common.tasks.FileTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path

class JbpmResourcesGenerator {
    private static final AntBuilder ant = AntUtil.builder()

    Path jbpmSrcDir
    Path destDir
    String processName
    String groupId
    String version

    public void generateProject(){
        Path resDir = destDir.resolve("src").resolve("main").resolve("resources")

        //setup directories
        FileTasks.mkdirs(destDir.resolve("src").resolve("main").resolve("java"))
        FileTasks.mkdirs(destDir.resolve("src").resolve("test").resolve("java"))
        FileTasks.mkdirs(destDir.resolve("src").resolve("test").resolve("resources"))

        //copy files
        ant.copy(todir: resDir.resolve("META-INF")){
            fileset(dir: jbpmSrcDir.resolve("META-INF")){ }
        }
        ant.copy(todir: destDir){
            fileset(file: jbpmSrcDir.resolve("project.imports")){ }
        }

        //generate pom.xml
        generatePom()
    }

    private void generatePom(){
        FileTasks.copyFileIntoFolder(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/jbpm/pom.xml"), destDir)
        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("GROUP_ID", groupId)
        replacements.put("PROCESS_NAME", processName)
        replacements.put("_VERSION_", version)
        FileTasks.replaceTokensInFile(destDir.resolve("pom.xml"), replacements)
    }
}

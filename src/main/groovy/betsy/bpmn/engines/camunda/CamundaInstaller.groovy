package betsy.bpmn.engines.camunda

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.ZipTasks

import java.nio.file.Path

class CamundaInstaller {
    private static final AntBuilder ant = AntUtil.builder()

    Path destinationDir

    String fileName = "camunda-bpm-tomcat-7.0.0-Final.zip"
    String groovyFile = "groovy-all-2.2.0.jar"
    String tomcatName

    public void install() {
        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        NetworkTasks.downloadFileFromBetsyRepo(fileName)
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir)

        NetworkTasks.downloadFileFromBetsyRepo(groovyFile);
        FileTasks.copyFileIntoFolder(Configuration.getDownloadsDir().resolve(groovyFile), tomcatDestinationDir.resolve("lib"))

        FileTasks.createFile(destinationDir.resolve("camunda_startup.bat"), "cd ${tomcatBinFolder.toAbsolutePath()} && call startup.bat")
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.bat"), "cd ${tomcatBinFolder.toAbsolutePath()} && call shutdown.bat")

        FileTasks.createFile(destinationDir.resolve("camunda_startup.sh"), "cd ${tomcatBinFolder.toAbsolutePath()} && ./startup.sh")
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.sh"), "cd ${tomcatBinFolder.toAbsolutePath()} && ./shutdown.sh")

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()))
    }

    public Path getTomcatDestinationDir() {
        destinationDir.resolve("server").resolve(tomcatName)
    }

    public Path getTomcatBinFolder() {
        tomcatDestinationDir.resolve("bin")
    }

    @Override
    public String toString() {
        return "CamundaInstaller{" +
                "destinationDir='" + destinationDir + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

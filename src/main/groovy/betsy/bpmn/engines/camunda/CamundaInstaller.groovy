package betsy.bpmn.engines.camunda

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks

import java.nio.file.Path

class CamundaInstaller {
    private static final AntBuilder ant = AntUtil.builder()

    Path destinationDir

    String fileName = "camunda-bpm-tomcat-7.0.0-Final.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"
    String groovyFile = "groovy-all-2.2.0.jar"
    String groovyUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${groovyFile}"
    String tomcatName = "apache-tomcat-7.0.33"

    public void install() {
        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: groovyUrl
        }

        ant.unzip src: Configuration.getDownloadsDir().resolve(fileName),
                dest: destinationDir

        ant.copy(toDir: tomcatDestinationDir.resolve("lib"), file: Configuration.get("downloads.dir") + "/" + groovyFile)

        FileTasks.createFile(destinationDir.resolve("camunda_startup.bat"), "cd ${tomcatBinFolder.toAbsolutePath()} && call startup.bat")
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.bat"), "cd ${tomcatBinFolder.toAbsolutePath()} && call shutdown.bat")

        FileTasks.createFile(destinationDir.resolve("camunda_startup.sh"), "cd ${tomcatBinFolder.toAbsolutePath()} && ./startup.sh")
        FileTasks.createFile(destinationDir.resolve("camunda_shutdown.sh"), "cd ${tomcatBinFolder.toAbsolutePath()} && ./shutdown.sh")

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()))
    }

    public Path getTomcatDestinationDir() {
        destinationDir.resolve("server/" + tomcatName)
    }

    public Path getTomcatBinFolder() {
        tomcatDestinationDir.resolve("bin")
    }

    @Override
    public String toString() {
        return "CamundaInstaller{" +
                "destinationDir='" + destinationDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}

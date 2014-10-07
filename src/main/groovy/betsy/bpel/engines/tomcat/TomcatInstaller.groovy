package betsy.bpel.engines.tomcat

import ant.tasks.AntUtil
import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks

import java.nio.file.Path

class TomcatInstaller {

    AntBuilder ant = AntUtil.builder()

    Path destinationDir
    String additionalVmParam = ""

    String fileName = "apache-tomcat-7.0.53-windows-x64.zip"
    String tomcatName = "apache-tomcat-7.0.53"

    public void install() {
        FileTasks.mkdirs(Configuration.downloadsDir)
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.unzip src: Configuration.downloadsDir.resolve(fileName), dest: destinationDir

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.bat"), """SET CATALINA_OPTS=-Xmx3048M -XX:MaxPermSize=2048m ${additionalVmParam}
cd ${tomcatBinFolder.toAbsolutePath()} && call startup.bat""")
        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.bat"), "cd ${tomcatBinFolder.toAbsolutePath()} && call shutdown.bat")

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.sh"), """CATALINA_OPTS=\"-Xmx3048M -XX:MaxPermSize=2048m ${additionalVmParam}\"
cd ${tomcatBinFolder.toAbsolutePath()} && ./startup.sh""")
        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.sh"), "cd ${tomcatBinFolder.toAbsolutePath()} && ./shutdown.sh")

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()))
    }

    public Path getTomcatDestinationDir() {
        destinationDir.resolve(tomcatName)
    }

    public Path getTomcatWebappsFolder() {
        getTomcatDestinationDir().resolve("webapps")
    }

    public void deployWar(Path war) {
        FileTasks.copyFileIntoFolder(war, getTomcatWebappsFolder())
    }

    public Path getTomcatBinFolder() {
        tomcatDestinationDir.resolve("bin")
    }

}

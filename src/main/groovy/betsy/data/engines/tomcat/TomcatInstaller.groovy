package betsy.data.engines.tomcat

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks

import java.nio.file.Path

class TomcatInstaller {

    AntBuilder ant = AntUtil.builder()

    Path destinationDir
    String additionalVmParam = ""

    String tomcatArchiveFileName = "apache-tomcat-7.0.26-windows-x64.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-tomcat-7.0.26-windows-x64.zip"
    String tomcatName = "apache-tomcat-7.0.26"

    public void install() {
        FileTasks.mkdirs(Configuration.downloadsDir)
        ant.get dest: Configuration.downloadsDir, skipexisting: true, {
            ant.url url: downloadUrl
        }

        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.unzip src: Configuration.downloadsDir.resolve(tomcatArchiveFileName), dest: destinationDir

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

    public Path getTomcatBinFolder() {
        tomcatDestinationDir.resolve("bin")
    }

}

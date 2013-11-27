package betsy.data.engines.tomcat

import ant.tasks.AntUtil
import betsy.Configuration
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
        FileTasks.mkdirs(Configuration.getPath("downloads.dir"))
        ant.get dest: Configuration.get("downloads.dir"), skipexisting: true, {
            ant.url url: downloadUrl
        }

        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(tomcatArchiveFileName), dest: destinationDir

        ant.echo file: destinationDir.resolve("tomcat_startup.bat"), message: getStartupScript()
        ant.echo file: destinationDir.resolve("tomcat_shutdown.bat"), message: getShutdownScript()
    }

    public Path getTomcatDestinationDir() {
        destinationDir.resolve(tomcatName)
    }

    public Path getTomcatBinFolder() {
        tomcatDestinationDir.resolve("bin")
    }

    public String getStartupScript() {
        """SET "CATALINA_OPTS=-Xmx3048M -XX:MaxPermSize=2048m ${additionalVmParam}
cd ${tomcatBinFolder.toAbsolutePath()} && call startup.bat"""
    }

    public String getShutdownScript() {
        "cd ${tomcatBinFolder.toAbsolutePath()} && call shutdown.bat"
    }

}

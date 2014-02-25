package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.config.Configuration
import betsy.data.engines.tomcat.TomcatInstaller
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 09:49
 */
class CamundaInstaller {
    private static final AntBuilder ant = AntUtil.builder()

    Path destinationDir

    String fileName = "camunda-bpm-tomcat-7.0.0-Final.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"
    String tomcatName = "apache-tomcat-7.0.33"

    public void install() {
        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(fileName),
                dest: destinationDir
        //ant.copy file: Paths.get(BpelgInstaller.class.getResource("/bpelg/log4j.properties").toURI()),
        //        todir: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/bpel-g/WEB-INF"), overwrite: true

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
                /*", downloadUrl='" + downloadUrl + '\'' +*/
                '}';
    }
}

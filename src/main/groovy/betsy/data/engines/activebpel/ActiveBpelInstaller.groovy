package betsy.data.engines.activebpel

import ant.tasks.AntUtil
import betsy.Configuration
import betsy.data.engines.tomcat.TomcatInstaller
import betsy.tasks.ConsoleTasks

import java.nio.file.Path
import java.nio.file.Paths

class ActiveBpelInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/active-bpel")
    String fileName = "activebpel-5.0.2-bin.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    public void install() {
        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl",
                tomcatArchiveFileName: "apache-tomcat-5.5.36.zip", tomcatName: "apache-tomcat-5.5.36",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-tomcat-5.5.36.zip")
        tomcatInstaller.install()

        ant.get(dest: Configuration.getPath("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(fileName), dest: serverDir

        LinkedHashMap<String, String> environment = ["CATALINA_HOME": tomcatInstaller.tomcatDestinationDir.toString()]
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(serverDir.resolve("activebpel-5.0.2"), "install.bat"), environment)
    }


    @Override
    public String toString() {
        return "ActiveBpelInstaller{" +
                "serverDir='" + serverDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}

package betsy.bpel.engines.activebpel

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.bpel.engines.tomcat.TomcatInstaller
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import betsy.tasks.NetworkTasks

import java.nio.file.Path
import java.nio.file.Paths

class ActiveBpelInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/active-bpel")
    String fileName = "activebpel-5.0.2-bin.zip"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl",
                fileName: "apache-tomcat-5.5.36.zip", tomcatName: "apache-tomcat-5.5.36")
        tomcatInstaller.install()

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ant.unzip src: Configuration.downloadsDir.resolve(fileName), dest: serverDir

        LinkedHashMap<String, String> environment = ["CATALINA_HOME": tomcatInstaller.tomcatDestinationDir.toString()]
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(serverDir.resolve("activebpel-5.0.2"), "install.bat"), environment)
    }


    @Override
    public String toString() {
        return "ActiveBpelInstaller{" +
                "serverDir='" + serverDir + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

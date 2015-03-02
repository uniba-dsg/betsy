package betsy.bpel.engines.activebpel

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.engines.tomcat.TomcatInstaller
import betsy.common.tasks.ConsoleTasks
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks

import java.nio.file.Path
import java.nio.file.Paths

class ActiveBpelInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/active-bpel")
    String fileName = "activebpel-5.0.2-bin.zip"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = TomcatInstaller.v5(serverDir);
        tomcatInstaller.setAdditionalVmParam("-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl");
        tomcatInstaller.install()

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ant.unzip src: Configuration.downloadsDir.resolve(fileName), dest: serverDir

        LinkedHashMap<String, String> environment = ["CATALINA_HOME": tomcatInstaller.getDestinationDir().toString()]
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

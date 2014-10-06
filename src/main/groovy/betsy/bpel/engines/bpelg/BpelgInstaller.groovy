package betsy.bpel.engines.bpelg

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.bpel.engines.tomcat.TomcatInstaller
import betsy.tasks.FileTasks
import betsy.tasks.NetworkTasks

import java.nio.file.Path
import java.nio.file.Paths

class BpelgInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir

    String fileName = "bpel-g-5.3.war"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl")
        tomcatInstaller.install()

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ant.unzip src: Configuration.downloadsDir.resolve(fileName),
                dest: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps").resolve("bpel-g")
        ant.copy file: Paths.get(BpelgInstaller.class.getResource("/bpelg/log4j.properties").toURI()),
                todir: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/bpel-g/WEB-INF"), overwrite: true

    }


    @Override
    public String toString() {
        return "BpelgInstaller{" +
                "serverDir='" + serverDir + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}

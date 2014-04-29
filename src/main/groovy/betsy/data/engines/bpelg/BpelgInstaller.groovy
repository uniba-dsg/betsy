package betsy.data.engines.bpelg

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.data.engines.tomcat.TomcatInstaller
import betsy.tasks.FileTasks

import java.nio.file.Path
import java.nio.file.Paths

class BpelgInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir

    String fileName = "bpel-g-5.3.war"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl")
        tomcatInstaller.install()

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

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
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}

package betsy.data.engines.bpelg

import ant.tasks.AntUtil
import betsy.Configuration
import betsy.data.engines.tomcat.TomcatInstaller

import java.nio.file.Path
import java.nio.file.Paths

class BpelgInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/bpelg")

    String fileName = "bpel-g-5.3.war"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    String databaseName = "h2-1.2.122.jar"
    String databaseDownloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${databaseName}"

    public void install() {
        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir,
                additionalVmParam: "-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl")
        tomcatInstaller.install()

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(fileName),
                dest: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps").resolve("bpel-g")
        ant.copy file: "src/main/resources/bpelg/log4j.properties",
                todir: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/bpel-g/WEB-INF"), overwrite: true

        ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: databaseDownloadUrl
        }
        ant.copy file: Configuration.getPath("downloads.dir").resolve(databaseName),
                tofile: serverDir.resolve(tomcatInstaller.tomcatName).resolve("lib").resolve(databaseName)
    }


    @Override
    public String toString() {
        return "BpelgInstaller{" +
                "serverDir='" + serverDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", databaseDownloadUrl='" + databaseDownloadUrl + '\'' +
                '}';
    }
}

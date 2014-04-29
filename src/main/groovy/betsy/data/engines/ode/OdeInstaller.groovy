package betsy.data.engines.ode

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.data.engines.tomcat.TomcatInstaller
import betsy.tasks.FileTasks

import java.nio.file.Path
import java.nio.file.Paths

class OdeInstaller {

    AntBuilder ant = AntUtil.builder()

    String fileName = "apache-ode-war-1.3.5.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"
    Path serverDir = Paths.get("server/ode")
    String odeName = "apache-ode-war-1.3.5"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir)
        tomcatInstaller.install()

        Path downloadDir = Configuration.downloadsDir

        ant.get(dest: downloadDir, skipexisting: true) {
            ant.url url: downloadUrl
        }

        ant.unzip src: downloadDir.resolve(fileName), dest: serverDir
        ant.unzip src: odeWar, dest: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/ode")
        ant.copy file: Paths.get(OdeInstaller.class.getResource("/ode/log4j.properties").toURI()), todir: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/ode/WEB-INF/classes"), overwrite: true
    }

    Path getOdeWar() {
        serverDir.resolve(odeName).resolve("ode.war")
    }

}

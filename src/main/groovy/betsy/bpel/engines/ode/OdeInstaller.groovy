package betsy.bpel.engines.ode

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.engines.tomcat.TomcatInstaller
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path
import java.nio.file.Paths

class OdeInstaller {

    AntBuilder ant = AntUtil.builder()

    String fileName = "apache-ode-war-1.3.5.zip"
    Path serverDir = Paths.get("server/ode")
    String odeName = "apache-ode-war-1.3.5"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = TomcatInstaller.v7(serverDir)
        tomcatInstaller.install()

        Path downloadDir = Configuration.downloadsDir
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ant.unzip src: downloadDir.resolve(fileName), dest: serverDir
        ant.unzip src: odeWar, dest: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/ode")
        ant.copy file: ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode/log4j.properties"), todir: serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps/ode/WEB-INF/classes"), overwrite: true
    }

    Path getOdeWar() {
        serverDir.resolve(odeName).resolve("ode.war")
    }

}

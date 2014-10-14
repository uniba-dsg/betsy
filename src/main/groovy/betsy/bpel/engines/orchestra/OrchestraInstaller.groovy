package betsy.bpel.engines.orchestra

import ant.tasks.AntUtil
import betsy.common.config.Configuration;
import betsy.bpel.engines.tomcat.TomcatInstaller
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.ZipTasks

import java.nio.file.Path
import java.nio.file.Paths

class OrchestraInstaller {

    AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/orchestra")
    String fileName = "orchestra-cxf-tomcat-4.9.0.zip"
    Path installDir = serverDir.resolve("orchestra-cxf-tomcat-4.9.0")

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = new TomcatInstaller(destinationDir: serverDir)
        tomcatInstaller.install()

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.downloadsDir.resolve(fileName), serverDir)

        ant.propertyfile(file: installDir.resolve("conf").resolve("install.properties")) {
            entry key: "catalina.home", value: "../apache-tomcat-7.0.26"
        }

        ant.ant target: "install", dir: installDir
    }

}

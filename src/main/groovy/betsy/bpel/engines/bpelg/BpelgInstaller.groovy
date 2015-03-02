package betsy.bpel.engines.bpelg

import ant.tasks.AntUtil
import betsy.common.config.Configuration
import betsy.common.engines.tomcat.TomcatInstaller
import betsy.common.tasks.FileTasks
import betsy.common.tasks.NetworkTasks
import betsy.common.tasks.ZipTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path

class BpelgInstaller {

    private static final AntBuilder ant = AntUtil.builder()

    Path serverDir

    String fileName = "bpel-g-5.3.war"

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir)

        TomcatInstaller tomcatInstaller = TomcatInstaller.v7(serverDir);
        tomcatInstaller.setAdditionalVmParam("-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl");
        tomcatInstaller.install()

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.downloadsDir.resolve(fileName),
                serverDir.resolve(tomcatInstaller.tomcatName).resolve("webapps").resolve("bpel-g"))

        ant.copy file: ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg/log4j.properties"),
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

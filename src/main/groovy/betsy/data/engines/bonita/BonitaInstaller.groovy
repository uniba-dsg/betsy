package betsy.data.engines.bonita

import ant.tasks.AntUtil
import betsy.config.Configuration
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks

import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: stavorndran
 * Date: 03.03.14
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
class BonitaInstaller {
    private static final AntBuilder ant = AntUtil.builder()

    Path destinationDir

    String fileName = "BonitaBPMCommunity-6.2.2-Tomcat-6.0.37.zip"
    String downloadUrl = "https://lspi.wiai.uni-bamberg.de/svn/betsy/${fileName}"

    public void install() {
        FileTasks.deleteDirectory(destinationDir)
        FileTasks.mkdirs(destinationDir)

        /*ant.get(dest: Configuration.get("downloads.dir"), skipexisting: true) {
            ant.url url: downloadUrl
        }*/

        ant.unzip src: Configuration.getPath("downloads.dir").resolve(fileName),
                dest: destinationDir
        ant.copy(file: "bpmnRes/bonita/server.xml", todir: destinationDir.resolve("conf"))
    }

    @Override
    public String toString() {
        return "BonitaInstaller{" +
                "destinationDir='" + destinationDir + '\'' +
                ", fileName='" + fileName + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}

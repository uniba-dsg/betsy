package betsy.bpel.engines.petalsesb

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.tasks.FileTasks
import betsy.tasks.NetworkTasks

import java.nio.file.Path
import java.nio.file.Paths

class PetalsEsbInstaller {

    AntBuilder ant = AntUtil.builder()

    Path serverDir = Paths.get("server/petalsesb")

    String fileName = "petals-esb-distrib-4.0.zip"

    Path targetEsbInstallDir = serverDir.resolve("petals-esb-4.0/install")
    Path bpelComponentPath = serverDir.resolve("petals-esb-distrib-4.0/esb-components/petals-se-bpel-1.1.0.zip")
    Path soapComponentPath = serverDir.resolve("petals-esb-distrib-4.0/esb-components/petals-bc-soap-4.1.0.zip")
    Path sourceFile = serverDir.resolve("petals-esb-distrib-4.0/esb/petals-esb-4.0.zip")

    public void install() {
        FileTasks.deleteDirectory(serverDir)
        FileTasks.mkdirs(serverDir)

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ant.unzip src: Configuration.downloadsDir.resolve(fileName), dest: serverDir
        ant.unzip src: sourceFile, dest: serverDir

        // install bpel service engine and binding connector for soap messages
        ant.copy file: bpelComponentPath, todir: targetEsbInstallDir
        ant.copy file: soapComponentPath, todir: targetEsbInstallDir
    }
}

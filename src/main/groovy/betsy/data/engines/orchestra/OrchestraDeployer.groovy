package betsy.data.engines.orchestra

import ant.tasks.AntUtil
import betsy.Configuration
import betsy.tasks.ConsoleTasks

import java.nio.file.Path

class OrchestraDeployer {

    final AntBuilder ant = AntUtil.builder()

    Path orchestraHome
    Path packageFilePath

    void deploy() {
        Path antBinFolder = Configuration.getPath("ant.home").resolve("bin").toAbsolutePath()
        Path antBat = antBinFolder.resolve("ant.bat")

        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(orchestraHome, antBat).values("deploy", "-Dbar=${packageFilePath.toAbsolutePath()}"))
    }

}

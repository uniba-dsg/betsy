package betsy.data.engines.orchestra

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.tasks.ConsoleTasks

import java.nio.file.Path

class OrchestraDeployer {

    final AntBuilder ant = AntUtil.builder()

    Path orchestraHome
    Path packageFilePath
    Path antBinFolder

    void deploy() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(orchestraHome, antBinFolder.resolve("ant.bat")).values("deploy", "-Dbar=${packageFilePath.toAbsolutePath()}"))
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(orchestraHome, antBinFolder.resolve("ant")).values("deploy", "-Dbar=${packageFilePath.toAbsolutePath()}"))
    }

}

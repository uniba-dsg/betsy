package betsy.bpel.engines.orchestra;

import betsy.common.tasks.ConsoleTasks;

import java.nio.file.Path;

public class OrchestraDeployer {
    public void deploy() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(
                ConsoleTasks.CliCommand.build(orchestraHome, antBinFolder.resolve("ant.bat")).
                        values("deploy", "-Dbar=" + packageFilePath.toAbsolutePath()));
        ConsoleTasks.executeOnUnixAndIgnoreError(
                ConsoleTasks.CliCommand.build(orchestraHome, antBinFolder.resolve("ant")).
                        values("deploy", "-Dbar=" + packageFilePath.toAbsolutePath()));
    }

    public Path getOrchestraHome() {
        return orchestraHome;
    }

    public void setOrchestraHome(Path orchestraHome) {
        this.orchestraHome = orchestraHome;
    }

    public Path getPackageFilePath() {
        return packageFilePath;
    }

    public void setPackageFilePath(Path packageFilePath) {
        this.packageFilePath = packageFilePath;
    }

    public Path getAntBinFolder() {
        return antBinFolder;
    }

    public void setAntBinFolder(Path antBinFolder) {
        this.antBinFolder = antBinFolder;
    }

    private Path orchestraHome;
    private Path packageFilePath;
    private Path antBinFolder;
}

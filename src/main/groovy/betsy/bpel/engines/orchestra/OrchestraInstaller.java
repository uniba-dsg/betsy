package betsy.bpel.engines.orchestra;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.*;

import java.nio.file.Path;

public class OrchestraInstaller {

    private final Path serverDir;

    public OrchestraInstaller(Path serverPath) {
        this.serverDir = serverPath;
    }

    public Path getInstallDir() {
        return serverDir.resolve("orchestra-cxf-tomcat-4.9.0");
    }

    public Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir);

        TomcatInstaller tomcatInstaller = TomcatInstaller.v7(serverDir);
        tomcatInstaller.install();

        String fileName = "orchestra-cxf-tomcat-4.9.0.zip";
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), serverDir);

        PropertyTasks.setPropertyInPropertiesFile(getInstallDir().resolve("conf").resolve("install.properties"), "catalina.home", "../" + tomcatInstaller.getTomcatName());

        ConsoleTasks.setupAnt(getAntPath());

        // clean up data (with db and config files in the users home directory)
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getInstallDir(), getAntPath().toAbsolutePath().toString() + "/ant install"));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getInstallDir(), getAntPath().toAbsolutePath().toString() + "/ant").values("install"));
    }

}

package betsy.bpel.engines.ode;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.Objects;

public class OdeInstaller {

    private final String fileName;
    private final Path serverDir;

    public OdeInstaller(Path serverDir, String fileName) {
        this.serverDir = Objects.requireNonNull(serverDir);
        this.fileName = Objects.requireNonNull(fileName);
    }

    public String getOdeName() {
        return FileTasks.getFilenameWithoutExtension(fileName);
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(serverDir);

        TomcatInstaller installer = TomcatInstaller.v7(serverDir);
        installer.install();

        Path downloadDir = Configuration.getDownloadsDir();
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(downloadDir.resolve(fileName), serverDir);
        ZipTasks.unzip(getOdeWar(), serverDir.resolve(installer.getTomcatName()).resolve("webapps/ode"));

        FileTasks.copyFileIntoFolderAndOverwrite(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode/log4j.properties"),
                serverDir.resolve(installer.getTomcatName()).resolve("webapps/ode/WEB-INF/classes"));

    }

    public Path getOdeWar() {
        return serverDir.resolve(getOdeName()).resolve("ode.war");
    }

}

package betsy.bpel.engines.bpelg;

import betsy.common.config.Configuration;
import betsy.common.engines.tomcat.TomcatInstaller;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.Objects;

public class BpelgInstaller {

    private final Path path;
    private final String warFileName;

    public BpelgInstaller(Path serverDir, String fileName) {
        this.path = Objects.requireNonNull(serverDir);
        this.warFileName = Objects.requireNonNull(fileName);
    }

    public void install() {
        // setup engine folder
        FileTasks.mkdirs(path);

        TomcatInstaller installer = TomcatInstaller.v7(path);
        installer.setAdditionalVmParam("-Djavax.xml.soap.MessageFactory=org.apache.axis.soap.MessageFactoryImpl");
        installer.install();

        NetworkTasks.downloadFileFromBetsyRepo(warFileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(warFileName),
                path.resolve(installer.getTomcatName()).resolve("webapps").resolve("bpel-g"));

        FileTasks.copyFileIntoFolderAndOverwrite(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg/log4j.properties"),
                path.resolve(installer.getTomcatName()).resolve("webapps/bpel-g/WEB-INF"));
    }

    @Override
    public String toString() {
        return "BpelgInstaller{" + "path='" + path + "\'" + ", warFileName='" + warFileName + "\'" + "}";
    }


}

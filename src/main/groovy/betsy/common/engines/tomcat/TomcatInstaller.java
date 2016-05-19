package betsy.common.engines.tomcat;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

public class TomcatInstaller {

    private final Path destinationDir;
    private final String fileName;
    private final String tomcatName;
    private String additionalVmParam = "";

    public TomcatInstaller(Path destinationDir, String fileName, String tomcatName) {
        this.destinationDir = destinationDir;
        this.fileName = fileName;
        this.tomcatName = tomcatName;
    }

    public static TomcatInstaller v7(Path parentFolder) {
        return new TomcatInstaller(parentFolder, "apache-tomcat-7.0.53-windows-x64.zip", "apache-tomcat-7.0.53");
    }

    public static TomcatInstaller v5(Path parentFolder) {
        return new TomcatInstaller(parentFolder, "apache-tomcat-5.5.36.zip", "apache-tomcat-5.5.36");
    }

    public void install() {
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.bat"), "SET CATALINA_OPTS=-Xmx3048M -XX:MaxPermSize=2048m " +
                additionalVmParam + "\ncd " + getTomcat().getTomcatBinDir().toAbsolutePath() + " && start \"" + getTomcatName() + "\" /min startup.bat");
        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.bat"), "cd " +
                getTomcat().getTomcatBinDir().toAbsolutePath() + " && call shutdown.bat");

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.sh"), "CATALINA_OPTS=\"-Xmx3048M -XX:MaxPermSize=2048m\"" +
                additionalVmParam + "\ncd " + getTomcat().getTomcatBinDir().toAbsolutePath() + " && ./startup.sh");

        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.sh"), "cd " +
                getTomcat().getTomcatBinDir().toAbsolutePath() + " && ./shutdown.sh");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()));

        FileTasks.replaceLine(getTomcat().getTomcatBinDir().resolve("startup.bat"), "call \"%EXECUTABLE%\" start %CMD_LINE_ARGS%", "call \"%EXECUTABLE%\" run %CMD_LINE_ARGS%");
    }

    public Tomcat getTomcat() {
        return new Tomcat(destinationDir, tomcatName, 8080);
    }

    public Path getDestinationDir() {
        return destinationDir;
    }

    public String getAdditionalVmParam() {
        return additionalVmParam;
    }

    public void setAdditionalVmParam(String additionalVmParam) {
        this.additionalVmParam = additionalVmParam;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTomcatName() {
        return tomcatName;
    }

}

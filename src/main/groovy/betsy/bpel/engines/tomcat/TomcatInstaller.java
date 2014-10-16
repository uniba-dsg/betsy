package betsy.bpel.engines.tomcat;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;

public class TomcatInstaller {
    public void install() {
        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        FileTasks.deleteDirectory(destinationDir);
        FileTasks.mkdirs(destinationDir);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), destinationDir);

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.bat"), "SET CATALINA_OPTS=-Xmx3048M -XX:MaxPermSize=2048m " +
                additionalVmParam + "\ncd " + getTomcat().getTomcatBinDir().toAbsolutePath() + " && call startup.bat");
        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.bat"), "cd " +
                getTomcat().getTomcatBinDir().toAbsolutePath() + " && call shutdown.bat");

        FileTasks.createFile(destinationDir.resolve("tomcat_startup.sh"), "CATALINA_OPTS=\"-Xmx3048M -XX:MaxPermSize=2048m " +
                additionalVmParam + "\ncd" + getTomcat().getTomcatBinDir().toAbsolutePath() + " &&. / startup.sh");

        FileTasks.createFile(destinationDir.resolve("tomcat_shutdown.sh"), "cd " +
                getTomcat().getTomcatBinDir().toAbsolutePath() + " && ./shutdown.sh");

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777", destinationDir.toAbsolutePath().toString()));
    }

    public Tomcat getTomcat() {
        Tomcat tomcat = new Tomcat();
        tomcat.setEngineDir(destinationDir);
        tomcat.setTomcatName(tomcatName);

        return tomcat;
    }

    public Path getDestinationDir() {
        return destinationDir;
    }

    public void setDestinationDir(Path destinationDir) {
        this.destinationDir = destinationDir;
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

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTomcatName() {
        return tomcatName;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

    private Path destinationDir;
    private String additionalVmParam = "";
    private String fileName = "apache-tomcat-7.0.53-windows-x64.zip";
    private String tomcatName = "apache-tomcat-7.0.53";
}

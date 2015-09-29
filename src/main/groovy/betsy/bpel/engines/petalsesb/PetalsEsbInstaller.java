package betsy.bpel.engines.petalsesb;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PetalsEsbInstaller {

    private Path serverDir;
    private String fileName;
    private Path targetEsbInstallDir;
    private Path bpelComponentPath;
    private Path soapComponentPath;
    private Path sourceFile;
    private Path petalsBinFolder;
    private Path cliFile;

    public void install() {
        FileTasks.deleteDirectory(serverDir);
        FileTasks.mkdirs(serverDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        //unzip main esb component
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), serverDir);
        ZipTasks.unzip(sourceFile, serverDir);

        //unzip cli -- needed for shutting down the esb
        ZipTasks.unzip(cliFile, serverDir);

        // install bpel service engine and binding connector for soap messages
        FileTasks.copyFileIntoFolder(bpelComponentPath, targetEsbInstallDir);
        FileTasks.copyFileIntoFolder(soapComponentPath, targetEsbInstallDir);

        FileTasks.createFile(petalsBinFolder.resolve("start-petals.sh"), "export JAVA_HOME=$JAVA7_HOME\ncd \"" + petalsBinFolder.toAbsolutePath() + "\" && ./petals-esb.sh >/dev/null 2>&1 &");
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(petalsBinFolder, "chmod").values("+x", "start-petals.sh"));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(petalsBinFolder, "chmod").values("+x", "petals-esb.sh"));
    }

    public void setPetalsBinFolder(Path petalsBinFolder) {
        this.petalsBinFolder = petalsBinFolder;
    }

    public void setServerDir(Path serverDir) {
        this.serverDir = serverDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTargetEsbInstallDir(Path targetEsbInstallDir) {
        this.targetEsbInstallDir = targetEsbInstallDir;
    }

    public void setBpelComponentPath(Path bpelComponentPath) {
        this.bpelComponentPath = bpelComponentPath;
    }

    public void setSoapComponentPath(Path soapComponentPath) {
        this.soapComponentPath = soapComponentPath;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setCliFile(Path cliFile) { this.cliFile = cliFile;}

}

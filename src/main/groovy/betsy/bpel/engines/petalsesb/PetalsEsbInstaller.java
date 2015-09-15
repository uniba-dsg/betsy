package betsy.bpel.engines.petalsesb;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.NetworkTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PetalsEsbInstaller {

    public PetalsEsbInstaller(PetalsEsbEngine engine){
        this.engine = engine;
    }

    public void install() {
        FileTasks.deleteDirectory(serverDir);
        FileTasks.mkdirs(serverDir);

        NetworkTasks.downloadFileFromBetsyRepo(fileName);

        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(fileName), serverDir);
        ZipTasks.unzip(sourceFile, serverDir);

        // install bpel service engine and binding connector for soap messages
        FileTasks.copyFileIntoFolder(bpelComponentPath, targetEsbInstallDir);
        FileTasks.copyFileIntoFolder(soapComponentPath, targetEsbInstallDir);

        FileTasks.createFile(engine.getPetalsBinFolder().resolve("start-petals.sh"), "export JAVA_HOME=$JAVA7_HOME\ncd \"" + engine.getPetalsBinFolder().toAbsolutePath() + "\" && ./petals-esb.sh >/dev/null 2>&1 &");
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(engine.getPetalsBinFolder(), "chmod").values("+x", "start-petals.sh"));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(engine.getPetalsBinFolder(), "chmod").values("+x", "petals-esb.sh"));
    }

    public Path getServerDir() {
        return serverDir;
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

    public Path getTargetEsbInstallDir() {
        return targetEsbInstallDir;
    }

    public void setTargetEsbInstallDir(Path targetEsbInstallDir) {
        this.targetEsbInstallDir = targetEsbInstallDir;
    }

    public Path getBpelComponentPath() {
        return bpelComponentPath;
    }

    public void setBpelComponentPath(Path bpelComponentPath) {
        this.bpelComponentPath = bpelComponentPath;
    }

    public Path getSoapComponentPath() {
        return soapComponentPath;
    }

    public void setSoapComponentPath(Path soapComponentPath) {
        this.soapComponentPath = soapComponentPath;
    }

    public Path getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
    }

    private Path serverDir = Paths.get("server/petalsesb");
    private String fileName = "petals-esb-distrib-4.0.zip";
    private Path targetEsbInstallDir = serverDir.resolve("petals-esb-4.0/install");
    private Path bpelComponentPath = serverDir.resolve("petals-esb-distrib-4.0/esb-components/petals-se-bpel-1.1.0.zip");
    private Path soapComponentPath = serverDir.resolve("petals-esb-distrib-4.0/esb-components/petals-bc-soap-4.1.0.zip");
    private Path sourceFile = serverDir.resolve("petals-esb-distrib-4.0/esb/petals-esb-4.0.zip");
    private PetalsEsbEngine engine;
}

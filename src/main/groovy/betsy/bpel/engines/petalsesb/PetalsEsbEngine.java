package betsy.bpel.engines.petalsesb;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PetalsEsbEngine extends AbstractLocalBPELEngine {

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/petalsesb");
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "petalsesb", "4.0");
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return CHECK_URL + "/petals/services/" + process.getName() + "TestInterfaceService";
    }

    public Path getPetalsFolder() {
        return getServerPath().resolve(getPetalsFolderName());
    }

    protected String getPetalsFolderName() {
        return "petals-esb-4.0";
    }

    public Path getPetalsLogsFolder() {
        return getPetalsFolder().resolve("logs");
    }

    public Path getPetalsLogFile() {
        return getPetalsLogsFolder().resolve("petals.log");
    }

    public Path getPetalsBinFolder() {
        return getPetalsFolder().resolve("bin");
    }

    public Path getPetalsCliBinFolder() {return getServerPath().resolve("petals-cli-1.0.0/bin");}

    @Override
    public void storeLogs(BPELProcess process) {
        FileTasks.mkdirs(process.getTargetLogsPath());

        for (Path p : getLogs()) {
            FileTasks.copyFileIntoFolder(p, process.getTargetLogsPath());
        }
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.add(getPetalsLogFile());

        return result;
    }

    @Override
    public void startup() {
        Path pathToJava7 = Configuration.getJava7Home();
        Map<String,String> environment = new HashMap<>();
        environment.put("JAVA_HOME", pathToJava7.toString());

        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getPetalsBinFolder(), "petals-esb.bat"), environment);

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(getPetalsBinFolder(), getPetalsBinFolder().resolve("start-petals.sh").toAbsolutePath()));

        WaitTasks.waitFor(60 * 1000, 500, () -> FileTasks.hasFile(getPetalsLogFile()) &&
                FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.Container.Components.petals-bc-soap] : Component started") &&
                FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.Container.Components.petals-se-bpel] : Component started"));

        try {

            if (FileTasks.hasFileSpecificSubstring(getPetalsLogFile(), "[Petals.AutoLoaderService] : Error during the auto- installation of a component")) {
                throw new Exception("SOAP BC not installed correctly");
            }

        } catch (Exception ignore) {
            LOGGER.warn("SOAP BC Installation failed - shutdown, reinstall and start petalsesb again");
            shutdown();
            install();
            startup();
        }
    }

    @Override
    public void shutdown() {
        try {
            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.bat")).values("shutdown"));

            ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getPetalsCliBinFolder().resolve("petals-cli.sh").toString()));
            ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("sync"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.sh")).values("shutdown"));
        } catch (Exception e) {
            LOGGER.info("COULD NOT STOP ENGINE " + getName(), e);
        }
    }

    @Override
    public void install() {
        PetalsEsbInstaller installer = new PetalsEsbInstaller();
        installer.setServerDir(getServerPath());
        installer.setFileName("petals-esb-distrib-4.0.zip");
        installer.setTargetEsbInstallDir(getServerPath().resolve("petals-esb-4.0/install"));
        installer.setBpelComponentPath(getServerPath().resolve("petals-esb-distrib-4.0/esb-components/petals-se-bpel-1.1.0.zip"));
        installer.setSoapComponentPath(getServerPath().resolve("petals-esb-distrib-4.0/esb-components/petals-bc-soap-4.1.0.zip"));
        installer.setSourceFile(getServerPath().resolve("petals-esb-distrib-4.0/esb/petals-esb-4.0.zip"));
        installer.setCliFile(getServerPath().resolve("petals-esb-distrib-4.0/esb/petals-cli-1.0.0.zip"));
        installer.setPetalsBinFolder(getPetalsBinFolder());
        installer.install();
    }

    @Override
    public void deploy(BPELProcess process) {
        new PetalsEsbDeployer(getInstallationDir(), getPetalsLogFile()).deploy(process.getTargetPackageCompositeFilePath(), process.getName());
    }

    public Path getInstallationDir() {
        return getPetalsFolder().resolve("install");
    }

    @Override
    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        Path metaDir = process.getTargetProcessPath().resolve("META-INF");
        FileTasks.mkdirs(metaDir);

        XSLTTasks.transform(getXsltPath().resolve("create_jbi_from_bpel.xsl"), process.getTargetProcessFilePath(), metaDir.resolve("jbi.xml"));

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        Path testPartnerWsdl = process.getTargetProcessPath().resolve("TestPartner.wsdl");
        if (Files.exists(testPartnerWsdl)) {
            FileTasks.replaceTokenInFile(testPartnerWsdl, "TestService", process.getName() + "TestService");
        }


        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        new PetalsEsbCompositePackager(process).build();
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

    private static final Logger LOGGER = Logger.getLogger(PetalsEsbEngine.class);
    public static final String CHECK_URL = "http://localhost:8084";
}

package betsy.bpel.engines.petalsesb;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

public class PetalsEsbEngine extends AbstractLocalBPELEngine {

    private static final Logger LOGGER = Logger.getLogger(PetalsEsbEngine.class);
    public static final String CHECK_URL = "http://localhost:8084";

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/petalsesb");
    }

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "petalsesb", "4.0", LocalDate.of(2012,2,2), "LGPL 2.1+");
    }

    @Override
    public String getEndpointUrl(String name) {
        return CHECK_URL + "/petals/services/" + name + "TestInterfaceService";
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

        TimeoutRepository.getTimeout("PetalsEsb.startup").waitFor(() -> FileTasks.hasFile(getPetalsLogFile()) &&
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
    public void deploy(String name, Path path) {
        new PetalsEsbDeployer(getInstallationDir(), getPetalsLogFile())
                .deploy(path, name);
    }

    @Override
    public boolean isDeployed(QName process) {
        return new PetalsEsbDeployer(getInstallationDir(), getPetalsLogFile())
                .isDeployed(process.getLocalPart());
    }

    @Override
    public void undeploy(QName process) {
        new PetalsEsbDeployer(getInstallationDir(), getPetalsLogFile())
                .undeploy(process.getLocalPart());
    }

    public Path getInstallationDir() {
        return getPetalsFolder().resolve("install");
    }

    @Override
    public Path buildArchives(BPELProcess process) {
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

        return process.getTargetPackageCompositeFilePath();
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_URL);
    }

}

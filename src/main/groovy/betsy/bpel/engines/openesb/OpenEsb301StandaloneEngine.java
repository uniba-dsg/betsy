package betsy.bpel.engines.openesb;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.StringUtils;
import org.apache.log4j.Logger;
import betsy.common.timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class OpenEsb301StandaloneEngine extends AbstractLocalBPELEngine {

    private static final Logger LOGGER = Logger.getLogger(OpenEsb301StandaloneEngine.class);
    public static final String WEB_UI = "http://localhost:4848/webui";
    private String openEsbFolder = "OpenESB-SE-3.0.1";
    private String binariesFileName = "OpenESB-Quickstart-Standalone-v301-server-only.zip";
    private String adminBinariesFile = "openesb-oeadmin-1.0.1.jar";

    public OpenEsb301StandaloneEngine() {
    }

    public OpenEsb301StandaloneEngine(String openEsbFolder, String binariesFileName, String adminBinariesFile) {
        this.openEsbFolder = openEsbFolder;
        this.binariesFileName = binariesFileName;
        this.adminBinariesFile = adminBinariesFile;
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb");
    }

    @Override
    public void deploy(BPELProcess process) {

        String processName = process.getName();
        Path packageFilePath = process.getTargetPackageCompositeFilePath();

        LOGGER.info("Deploying " + processName + " from " + packageFilePath);

        // QUIRK path must always be in unix style, otherwise it is not correctly deployed

        Path passwordFilePath = getServerPath().resolve("password.txt");
        FileTasks.createFile(passwordFilePath, "OE_ADMIN_PASSWORD=admin");

        String[] deployParams = {"-jar", adminBinariesFile, "deploy-jbi-service-assembly",
                "--user", "admin",
                "--passwordfile", StringUtils.toUnixStyle(passwordFilePath),
                StringUtils.toUnixStyle(packageFilePath)};

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceFolder().resolve("lib"), "java").values(deployParams));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceFolder().resolve("lib"), "java").values(deployParams));

        String[] startParams = {"-jar", adminBinariesFile, "start-jbi-service-assembly",
                "--user", "admin",
                "--passwordfile", StringUtils.toUnixStyle(passwordFilePath),
                processName + "Application"};

        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceFolder().resolve("lib"), "java").values(startParams));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceFolder().resolve("lib"), "java").values(startParams));
    }

    public void buildDeploymentDescriptor(BPELProcess process) {
        Path metaDir = process.getTargetProcessPath().resolve("META-INF");
        Path catalogFile = metaDir.resolve("catalog.xml");
        FileTasks.mkdirs(metaDir);
        FileTasks.createFile(catalogFile, "<?xml version='1.0' encoding='UTF-8' standalone='no'?>\n<catalog xmlns='urn:oasis:names:tc:entity:xmlns:xml:catalog' prefer='system'>\n</catalog>");
        FileTasks.createFile(metaDir.resolve("MANIFEST.MF"), "Manifest-Version: 1.0");
        XSLTTasks.transform(getXsltPath().resolve("create_jbi_from_bpel.xsl"), process.getTargetProcessFilePath(), metaDir.resolve("jbi.xml"));
    }

    @Override
    public void buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        buildDeploymentDescriptor(process);

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        new OpenEsbCompositePackager(process).build();
    }

    @Override
    public String getEndpointUrl(BPELProcess process) {
        return "http://localhost:18181" + "/" + process.getName() + "TestInterface";
    }

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

        result.addAll(FileTasks.findAllInFolder(getInstanceFolder().resolve("logs")));

        return result;
    }

    @Override
    public void install() {
        FileTasks.deleteDirectory(getServerPath());
        FileTasks.mkdirs(getServerPath());

        NetworkTasks.downloadFileFromBetsyRepo(binariesFileName);
        ZipTasks.unzip(Configuration.getDownloadsDir().resolve(binariesFileName), getServerPath());

        FileTasks.createFile(getServerPath().resolve("start-openesb.bat"), "cd \"" + getInstanceBinFolder().toAbsolutePath() + "\" && start \"" + getName() + "\" /min openesb.bat");

        // goto folder
        // start openesb
        // put the process in the background
        FileTasks.createFile(getServerPath().resolve("start-openesb.sh"), "cd \"" + getInstanceBinFolder().toAbsolutePath() + "\" && ./openesb.sh >/dev/null 2>&1 &");
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getServerPath().resolve("start-openesb.sh").toString()));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getInstanceBinFolder().resolve("openesb.sh").toString()));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("sync"));
    }

    private Path getInstanceBinFolder() {
        return getInstanceFolder().resolve("bin");
    }

    private void installComponent(Path components, Path installFolder, String jarFilename) {
        LOGGER.info("installing component " + jarFilename);
        Callable<Boolean> condition = () -> FileTasks.hasFile(installFolder.resolve(jarFilename + "_installed"));

        try {
            if (condition.call()) {
                // already installed
                return;
            }
        } catch (Exception e) {
            LOGGER.info("Could not check condition whether component " + jarFilename + " is already installed");
        }
        FileTasks.copyFileIntoFolder(components.resolve(jarFilename), installFolder);

        TimeoutRepository.getTimeout("OpenEsb30x.installComponent").waitFor(condition);
    }

    @Override
    public void startup() {
        // start openesb.bat
        ConsoleTasks.executeOnWindows(ConsoleTasks.CliCommand.build(getServerPath(), "start-openesb.bat"));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build(getServerPath(), getServerPath().resolve("start-openesb.sh").toAbsolutePath()));
        TimeoutRepository.getTimeout("OpenEsb30x.startup.waitForUrl").waitForAvailabilityOfUrl(WEB_UI);

        // install bpelse
        Path components = getServerPath().resolve(openEsbFolder).resolve("OE-Components");
        Path installFolder = getInstanceFolder().resolve("server").resolve("jbi").resolve("autoinstall");

        TimeoutRepository.getTimeout("OpenEsb30x.startup.waitForStart").waitFor(() -> FileTasks.hasFolder(installFolder));

        installComponent(components, installFolder, "encoderlib.jar");
        installComponent(components, installFolder, "wsdlextlib.jar");
        installComponent(components, installFolder, "httpbc-full.jar");
        installComponent(components, installFolder, "bpelse.jar");
    }

    private Path getInstanceFolder() {
        return getServerPath().resolve(openEsbFolder).resolve("OE-Instance");
    }

    @Override
    public void shutdown() {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceBinFolder(), "openesb.bat").values("stop"));
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("taskkill").values("/FI", "WINDOWTITLE eq " + getName() + "*"));

        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getInstanceBinFolder(), getInstanceBinFolder().resolve("openesb.sh")).values("stop"));
    }

    @Override
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(WEB_UI);
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "openesb", "3.0.1", LocalDate.of(2015, 2, 13), "CDDL-1.0");
    }
}

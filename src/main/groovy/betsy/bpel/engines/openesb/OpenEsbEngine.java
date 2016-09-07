package betsy.bpel.engines.openesb;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.URLTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.timeouts.timeout.TimeoutRepository;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.OperatingSystem;

public class OpenEsbEngine extends AbstractLocalBPELEngine {

    private static final String CHECK_URL = "http://localhost:18181";
    private static final String CHECK_WHETHER_RUNNING_URL = "http://localhost:8383";

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb");
    }

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "openesb", "2.2", LocalDate.of(2009, 12, 1), "CDDL-1.0");
    }

    @Override
    public String getEndpointUrl(String name) {
        return CHECK_URL + "/" + name + "TestInterface";
    }

    @Override
    public List<Path> getLogs() {
        List<Path> result = new LinkedList<>();

        result.addAll(FileTasks.findAllInFolder(getGlassfishHome().resolve("domains/domain1/logs/"), "*.log"));

        return result;
    }

    public OpenEsbCLI getCli() {
        return new OpenEsbCLI(getGlassfishHome());
    }

    protected Path getGlassfishHome() {
        return getServerPath().resolve("glassfish");
    }

    @Override
    public void startup() {
        getCli().startDomain();
        TimeoutRepository.getTimeout("OpenEsb.startup").waitForAvailabilityOfUrl(CHECK_WHETHER_RUNNING_URL);
    }

    @Override
    public void shutdown() {
        getCli().stopDomain();
    }

    @Override
    public void install() {
        if (OperatingSystem.WINDOWS) {
            new OpenEsbInstaller(getServerPath(),
                    "glassfishesb-v2.2-full-installer-windows.exe",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/windows_state.xml.template")).install();
        } else {
            new OpenEsbInstaller(getServerPath(),
                    "glassfishesb-v2.2-full-installer-linux.sh",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/state.xml.template")).install();
        }
    }

    @Override
    public void deploy(String name, Path path) {
        OpenEsbDeployer deployer = new OpenEsbDeployer(getCli());
        Path tmpfolder = path.getParent().resolve("TMPFOLDER");
        FileTasks.mkdirs(tmpfolder);
        deployer.deploy(name, path, tmpfolder);
    }

    @Override
    public boolean isDeployed(QName process) {
        return false;
    }

    @Override
    public void undeploy(QName process) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public Path buildArchives(BPELProcess process) {
        getPackageBuilder().createFolderAndCopyProcessFilesToTarget(process);

        // engine specific steps
        buildDeploymentDescriptor(process);

        FileTasks.replaceTokenInFile(process.getTargetProcessPath().resolve("TestInterface.wsdl"), "TestInterfaceService", process.getName() + "TestInterfaceService");

        getPackageBuilder().replaceEndpointTokenWithValue(process);
        getPackageBuilder().replacePartnerTokenWithValue(process);
        getPackageBuilder().bpelFolderToZipFile(process);

        new OpenEsbCompositePackager(process).build();

        return process.getTargetPackageCompositeFilePath();
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
    public boolean isRunning() {
        return URLTasks.isUrlAvailable(CHECK_WHETHER_RUNNING_URL);
    }

}

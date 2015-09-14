package betsy.bpel.engines.openesb;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.OperatingSystem;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class OpenEsbEngine extends AbstractLocalBPELEngine {

    @Override
    public String getName() {
        return "openesb";
    }

    @Override
    public String getEndpointUrl(final BPELProcess process) {
        return CHECK_URL + "/" + process.getName() + "TestInterface";
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

        result.addAll(FileTasks.findAllInFolder(getGlassfishHome().resolve("domains/domain1/logs/")));

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
        WaitTasks.waitForAvailabilityOfUrl(15_000, 500, "http://localhost:8383");
    }

    @Override
    public void shutdown() {
        getCli().stopDomain();
    }

    @Override
    public void install() {
        if(OperatingSystem.WINDOWS) {
            new OpenEsbInstaller(getServerPath(),
                    "glassfishesb-v2.2-full-installer-windows.exe",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/state.xml.template")).install();
        } else {
            new OpenEsbInstaller(getServerPath(),
                    "glassfishesb-v2.2-full-installer-linux.sh",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb/state.xml.template")).install();
        }
    }

    @Override
    public void deploy(BPELProcess process) {
        OpenEsbDeployer deployer = new OpenEsbDeployer(getCli());
        deployer.deploy(process.getName(), process.getTargetPackageCompositeFilePath(), process.getTargetPath());
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
        return false;
    }

    public static String getCHECK_URL() {
        return CHECK_URL;
    }
    private static final String CHECK_URL = "http://localhost:18181";
}

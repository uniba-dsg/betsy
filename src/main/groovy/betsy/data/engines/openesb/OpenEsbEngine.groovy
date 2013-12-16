package betsy.data.engines.openesb

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine
import betsy.tasks.FileTasks

import java.nio.file.Path

class OpenEsbEngine extends LocalEngine {

    static final String CHECK_URL = "http://localhost:18181"

    @Override
    String getName() {
        "openesb"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${CHECK_URL}/${process.name}TestInterface"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        Path logsFolder = process.targetPath.resolve("logs")
        FileTasks.mkdirs(logsFolder)
        ant.copy(todir: logsFolder) {
            ant.fileset(dir: glassfishHome.resolve("domains/domain1/logs/"))
        }
    }

    OpenEsbCLI getCli() {
        new OpenEsbCLI(glassfishHome: getGlassfishHome())
    }

    protected Path getGlassfishHome() {
        serverPath.resolve("glassfish")
    }

    @Override
    void startup() {
        cli.startDomain()
        ant.waitfor(maxwait: "15", maxwaitunit: "second", checkevery: "500") {
            http url: CHECK_URL
        }
    }

    @Override
    void shutdown() {
        cli.stopDomain()
    }

    @Override
    void install() {
        new OpenEsbInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        new OpenEsbDeployer(cli: cli,
                processName: process.name,
                packageFilePath: process.targetPackageCompositeFilePath,
                tmpFolder: process.targetPath).deploy()
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        buildDeploymentDescriptor(process)
        ant.replace(file: process.targetBpelPath.resolve("TestInterface.wsdl"),
                token: "TestInterfaceService", value: "${process.name}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new OpenEsbCompositePackager(process: process).build()
    }

    void buildDeploymentDescriptor(BetsyProcess process) {
        Path metaDir = process.targetBpelPath.resolve("META-INF")
        Path catalogFile = metaDir.resolve("catalog.xml")
        FileTasks.mkdirs(metaDir)
        ant.echo file: catalogFile, message: """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog" prefer="system">
</catalog>
        """
        ant.echo file: metaDir.resolve("MANIFEST.MF"), message: "Manifest-Version: 1.0"
        ant.xslt(in: process.targetBpelFilePath, out: metaDir.resolve("jbi.xml"),
                style: xsltPath.resolve("create_jbi_from_bpel.xsl"))
    }


    @Override
    boolean isRunning() {
        return false;
    }

}

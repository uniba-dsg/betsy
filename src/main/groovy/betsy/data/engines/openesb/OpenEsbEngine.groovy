package betsy.data.engines.openesb

import betsy.data.BetsyProcess
import betsy.data.engines.LocalEngine

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
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${glassfishHome}/domains/domain1/logs/")
        }
    }

    OpenEsbCLI getCli() {
        new OpenEsbCLI(glassfishHome: getGlassfishHome())
    }

    private String getGlassfishHome() {
        "${serverPath}/glassfish"
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
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.name}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new OpenEsbCompositePackager(process: process).build()
    }

    void buildDeploymentDescriptor(BetsyProcess process) {
        String metaDir = process.targetBpelPath + "/META-INF"
        String catalogFile = "$metaDir/catalog.xml"
        ant.mkdir(dir: metaDir)
        ant.echo file: catalogFile, message: """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog" prefer="system">
</catalog>
        """
        ant.echo file: "$metaDir/MANIFEST.MF", message: "Manifest-Version: 1.0"
        ant.xslt(in: process.targetBpelFilePath, out: "$metaDir/jbi.xml", style: "$xsltPath/create_jbi_from_bpel.xsl")
    }


    @Override
    void failIfRunning() {
        // do nothing
    }

}

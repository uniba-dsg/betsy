package betsy.data.engines.openesb

import betsy.data.Engine
import betsy.data.BetsyProcess

class OpenEsbEngine extends Engine {

    static final String CHECK_URL = "http://localhost:18181"

    @Override
    String getName() {
        "openesb"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${CHECK_URL}/${process.bpelFileNameWithoutExtension}TestInterface"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${serverPath}/glassfish/domains/domain1/logs/")
        }
    }

    OpenEsbCLI getCli() {
        new OpenEsbCLI(ant: ant, serverPath: getServerPath())
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
        cli.forceRedeploy(process)
    }

    @Override
    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        buildDeploymentDescriptor(process)
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new OpenEsbCompositePackager(ant: ant, process: process).build()
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

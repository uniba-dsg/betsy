package betsy.data.engines.orchestra

import betsy.data.Engine
import betsy.data.BetsyProcess
import betsy.data.engines.tomcat.Tomcat

class OrchestraEngine extends Engine {

    @Override
    String getName() {
        "orchestra"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath)
    }

    @Override
    void install() {
        new OrchestraInstaller().install()
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/orchestra/${process.bpelFileNameWithoutExtension}TestInterface"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void deploy(BetsyProcess process) {
        new OrchestraCLI(serverPath: getServerPath(), ant: ant).deploy(process)
    }

    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

}

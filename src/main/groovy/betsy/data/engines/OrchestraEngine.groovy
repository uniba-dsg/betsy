package betsy.data.engines

import betsy.data.Engine
import betsy.data.Process
import betsy.data.engines.cli.OrchestraCLI
import betsy.data.engines.server.Tomcat

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
        ant.ant(antfile: "build.xml", target: getName())
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
    String getEndpointUrl(Process process) {
        "${tomcat.tomcatUrl}/orchestra/${process.bpelFileNameWithoutExtension}TestInterface"
    }

    @Override
    void storeLogs(Process process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void deploy(Process process) {
        new OrchestraCLI(serverPath: getServerPath(), ant: ant).deploy(process)
    }

    @Override
    void onPostDeployment(Process process) {
        // do nothing - as using synchronous deployment
    }

    public void buildArchives(Process process) {
        createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        replaceEndpointAndPartnerTokensWithValues(process)
        bpelFolderToZipFile(process)
    }

}

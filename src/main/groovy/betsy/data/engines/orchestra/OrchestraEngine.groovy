package betsy.data.engines.orchestra

import betsy.data.LocalEngine
import betsy.data.Process
import betsy.data.engines.server.Tomcat

class OrchestraEngine extends LocalEngine {

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
    void onPostDeployment() {
        // do nothing - as using synchronous deployment
    }

    @Override
    void onPostDeployment(Process process) {
        // do nothing - as using synchronous deployment
    }

    public void buildArchives(Process process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        packageBuilder.replaceEndpointTokenWithValue(process)
		packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

}

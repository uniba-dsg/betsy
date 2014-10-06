package betsy.bpel.engines.orchestra

import betsy.config.Configuration
import betsy.bpel.model.BetsyProcess
import betsy.bpel.engines.LocalEngine
import betsy.bpel.engines.tomcat.Tomcat
import betsy.tasks.FileTasks

class OrchestraEngine extends LocalEngine {

    @Override
    String getName() {
        "orchestra"
    }

    Tomcat getTomcat() {
        new Tomcat(engineDir: serverPath)
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
    boolean isRunning() {
        try {
            tomcat.checkIfIsRunning()
            return false;
        } catch (Exception ignore) {
            return true;
        }
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/orchestra/${process.name}TestInterface"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: tomcat.tomcatLogsDir)
        }
    }

    @Override
    void deploy(BetsyProcess process) {
        new OrchestraDeployer(
                orchestraHome: serverPath.resolve("orchestra-cxf-tomcat-4.9.0"),
                packageFilePath: process.targetPackageFilePath,
                antBinFolder: Configuration.antHome.resolve("bin").toAbsolutePath()
        ).deploy()
    }

    public void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)
    }

}

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
    String getDeploymentPrefix() {
        "${tomcat.tomcatUrl}/orchestra"
    }

    @Override
    void storeLogs(Process process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    String getDeploymentPostfix() {
        "TestInterface"
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
    void install() {
        ant.ant(antfile: "build.xml", target: getName())
    }

    @Override
    void deploy(Process process) {
        new OrchestraCLI(engine: this,ant: ant).deploy(process)
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }
}

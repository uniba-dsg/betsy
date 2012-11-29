package betsy.data.engines

import betsy.data.Engine
import betsy.data.Process
import betsy.data.engines.server.Tomcat
import betsy.data.WsdlOperation

/*
* Currently using in-memory mode for the engine
 */
class ActiveBpelEngine extends Engine{

    @Override
    String getName() {
        return "active-bpel"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath, tomcatName: "apache-tomcat-5.5.36")
    }

    @Override
    String getDeploymentPrefix() {
        "${tomcat.tomcatUrl}/active-bpel/services"
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/bpr"
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
        "TestInterfaceService"
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
        ant.copy(file: process.targetPackageFilePath, todir: deploymentDir)
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

}

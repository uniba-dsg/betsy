package betsy.data.engines.cli

import betsy.data.Engine
import betsy.data.Process


class OrchestraCLI {

    AntBuilder ant = new AntBuilder()
    Engine engine

    void deploy(Process process) {
        ant.exec(executable: "cmd", dir: "${engine.serverPath}/orchestra-cxf-tomcat-4.9.0") {
            arg(value: "/c")
            arg(value: "ant")
            arg(value: "deploy")
            arg(value: "-Dbar=${new File(process.targetPackageFilePath).absolutePath}")
        }
    }

}

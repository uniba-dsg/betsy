package betsy.data.engines.orchestra

import betsy.data.BetsyProcess

class OrchestraCLI {

    AntBuilder ant = new AntBuilder()
    String serverPath

    void deploy(BetsyProcess process) {
        ant.exec(executable: "cmd", dir: "${serverPath}/orchestra-cxf-tomcat-4.9.0") {
            arg(value: "/c")
            arg(value: "ant")
            arg(value: "deploy")
            arg(value: "-Dbar=${new File(process.targetPackageFilePath).absolutePath}")
        }
    }

}

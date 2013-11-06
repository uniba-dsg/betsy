package betsy.data.engines.orchestra

class OrchestraDeployer {

    AntBuilder ant = new AntBuilder()

    String orchestraHome
    String packageFilePath

    void deploy() {
        ant.exec(executable: "cmd", dir: orchestraHome) {
            arg(value: "/c")
            arg(value: "ant")
            arg(value: "deploy")
            arg(value: "-Dbar=${new File(packageFilePath).absolutePath}")
        }
    }

}

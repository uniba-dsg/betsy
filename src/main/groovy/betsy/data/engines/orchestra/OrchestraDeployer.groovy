package betsy.data.engines.orchestra

import ant.tasks.AntUtil

class OrchestraDeployer {

    final AntBuilder ant = AntUtil.builder()

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

package betsy.data.engines.openesb

import java.nio.file.Path

class OpenEsbDeployer {

    OpenEsbCLI cli

    String processName
    Path packageFilePath
    Path tmpFolder

    public void deploy() {
        cli.forceRedeploy(processName, packageFilePath, tmpFolder)
    }

}

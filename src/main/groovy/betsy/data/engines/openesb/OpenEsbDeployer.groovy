package betsy.data.engines.openesb

class OpenEsbDeployer {

    OpenEsbCLI cli

    String processName
    String packageFilePath
    String tmpFolder

    public void deploy() {
        cli.forceRedeploy(processName, packageFilePath, tmpFolder)
    }

}

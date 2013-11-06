package betsy.data.engines.bpelg

class BpelgDeployer {

    AntBuilder ant = new AntBuilder()

    String deploymentDirPath
    String processName
    String packageFilePath
    String logFilePath

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                and {
                    available file: "${deploymentDirPath}/work/ae_temp_${processName}_zip/deploy.xml"
                    or {
                        resourcecontains(resource: logFilePath, substring: "Deployment successful")
                        resourcecontains(resource: logFilePath, substring: "Deployment failed")
                    }
                }
            }
        }
    }
}

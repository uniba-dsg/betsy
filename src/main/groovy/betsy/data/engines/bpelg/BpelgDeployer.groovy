package betsy.data.engines.bpelg

class BpelgDeployer {

    AntBuilder ant = new AntBuilder()

    String deploymentDirPath
    String processName
    String packageFilePath
    String logFilePath
    int timeoutInSeconds = 100

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
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

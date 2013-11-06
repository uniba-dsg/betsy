package betsy.data.engines.ode

class OdeDeployer {

    AntBuilder ant = new AntBuilder()

    String packageFilePath
    String deploymentDirPath
    String logFilePath
    String processName

    public void deploy() {
        ant.unzip src: packageFilePath, dest: "$deploymentDirPath/${processName}"

        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                and {
                    available file: "$deploymentDirPath/${processName}.deployed"
                    or {
                        resourcecontains(resource: logFilePath, substring: "Deployment of artifact $processName successful")
                        resourcecontains(resource: logFilePath, substring: "Deployment of $processName failed")
                    }
                }
            }
        }
    }
}

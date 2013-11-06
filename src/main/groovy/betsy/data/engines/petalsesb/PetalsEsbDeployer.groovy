package betsy.data.engines.petalsesb

class PetalsEsbDeployer {

    AntBuilder ant = new AntBuilder()

    String deploymentDirPath
    String processName
    String packageFilePath
    String logFilePath

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.waitfor(maxwait: "100", maxwaitunit: "second") {
            and {
                not() { available(file: "$deploymentDirPath/${processName}Application.zip") }
                or {
                    resourcecontains(resource: logFilePath,
                            substring: "Service Assembly '${processName}Application' started")
                    resourcecontains(resource: logFilePath,
                            substring: "Service Assembly '${processName}Application' deployed with some SU deployment in failure")
                }
            }
        }
    }
}

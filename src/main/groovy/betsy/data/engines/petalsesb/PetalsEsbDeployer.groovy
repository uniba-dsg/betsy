package betsy.data.engines.petalsesb

import ant.tasks.AntUtil

class PetalsEsbDeployer {

    AntBuilder ant = AntUtil.builder()

    String deploymentDirPath
    String processName
    String packageFilePath
    String logFilePath
    int timeoutInSeconds = 100

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
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

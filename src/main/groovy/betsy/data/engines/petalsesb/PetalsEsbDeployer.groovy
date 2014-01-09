package betsy.data.engines.petalsesb

import ant.tasks.AntUtil

import java.nio.file.Path

class PetalsEsbDeployer {

    AntBuilder ant = AntUtil.builder()

    Path deploymentDirPath
    String processName
    Path packageFilePath
    Path logFilePath
    int timeoutInSeconds = 100

    public void deploy() {


        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
            and {
                not() { available(file: deploymentDirPath.resolve("${processName}Application.zip")) }
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

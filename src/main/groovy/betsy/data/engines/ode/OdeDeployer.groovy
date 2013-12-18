package betsy.data.engines.ode

import ant.tasks.AntUtil
import betsy.tasks.ConsoleTasks
import betsy.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path

class OdeDeployer {

    private static final Logger log = Logger.getLogger(OdeDeployer.class)

    private static final AntBuilder ant = AntUtil.builder()

    Path packageFilePath
    Path deploymentDirPath
    Path logFilePath
    String processName
    int timeoutInSeconds = 30

    public void deploy() {
        log.info this.toString()

        Path deploymentIndicatorFile = deploymentDirPath.resolve("${processName}.deployed")
        FileTasks.deleteFile(deploymentIndicatorFile)

        Path deploymentProcessNameDirPath = deploymentDirPath.resolve(processName)

        ant.unzip src: packageFilePath, dest: deploymentProcessNameDirPath

        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("--recursive", "777",
                deploymentProcessNameDirPath.toString()))

        ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
                and {
                    available file: deploymentIndicatorFile
                    or {
                        resourcecontains(resource: logFilePath, substring: "Deployment of artifact $processName successful")
                        resourcecontains(resource: logFilePath, substring: "Deployment of $processName failed")
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Deploying process $processName ($packageFilePath) to $deploymentDirPath " +
                "within at most $timeoutInSeconds seconds. Verified via substring in $logFilePath"
    }

}

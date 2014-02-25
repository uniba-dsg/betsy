package betsy.data.engines.camunda

import ant.tasks.AntUtil

import java.nio.file.Path

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 11:35
 */
class CamundaDeployer {
    private static final AntBuilder ant = AntUtil.builder()

    Path deploymentDirPath
    String processName
    Path packageFilePath
    Path logFilePath
    int timeoutInSeconds = 100

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        /*ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
                and {
                    available directory: deploymentDirPath.resolve(processName)
                    or {
                        resourcecontains(resource: logFilePath, substring: "Deployment successful")
                        resourcecontains(resource: logFilePath, substring: "Deployment failed")
                    }
                }
            }
        } */
    }


    @Override
    public String toString() {
        return "CamundaDeployer{" +
                "deploymentDirPath='" + deploymentDirPath + '\'' +
                ", processName='" + processName + '\'' +
                ", packageFilePath='" + packageFilePath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

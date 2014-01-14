package betsy.data.engines.bpelg

import ant.tasks.AntUtil

import java.nio.file.Path

class BpelgDeployer {

    private static final AntBuilder ant = AntUtil.builder()

    Path deploymentDirPath
    String processName
    Path packageFilePath
    Path logFilePath
    int timeoutInSeconds = 100

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
                and {
                    available file: deploymentDirPath.resolve("work/ae_temp_${processName}_zip/deploy.xml")
                    or {
                        resourcecontains(resource: logFilePath, substring: "Deployment successful")
                        resourcecontains(resource: logFilePath, substring: "Deployment failed")
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        return "BpelgDeployer{" +
                "deploymentDirPath='" + deploymentDirPath + '\'' +
                ", processName='" + processName + '\'' +
                ", packageFilePath='" + packageFilePath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }

}

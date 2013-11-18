package betsy.data.engines.activebpel

import ant.tasks.AntUtil

import java.nio.file.Path

class ActiveBpelDeployer {

    private static final AntBuilder ant = AntUtil.builder()

    Path packageFilePath
    Path deploymentDirPath
    Path logFilePath
    String processName
    int timeoutInSeconds = 100

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)

        // define custom condition
        ant.typedef(name: "httpcontains", classname: "ant.tasks.HttpContains")

        ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
                and {
                    available file: deploymentDirPath.resolve("work/ae_temp_${processName}_bpr/META-INF/catalog.xml")
                    resourcecontains(resource: logFilePath,
                            substring: "[${processName}.pdd]")
                    httpcontains(contains: "Running", url: "http://localhost:8080/BpelAdmin/")
                }
            }
        }
    }


    @Override
    public String toString() {
        return "ActiveBpelDeployer{" +
                "packageFilePath='" + packageFilePath + '\'' +
                ", deploymentDirPath='" + deploymentDirPath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", processName='" + processName + '\'' +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

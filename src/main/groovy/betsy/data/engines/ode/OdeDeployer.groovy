package betsy.data.engines.ode

import ant.tasks.AntUtil
import org.apache.log4j.Logger

class OdeDeployer implements Deployer{

    private static final Logger log = Logger.getLogger(OdeDeployer.class)

    AntBuilder ant = AntUtil.builder()

    String packageFilePath
    String deploymentDirPath
    String logFilePath   // TODO package and processName should be passed. the other data shared.
    String processName
    int timeoutInSeconds = 30

    @Override
    public void deploy() {
        log.info this.toString()

        ant.unzip src: packageFilePath, dest: "$deploymentDirPath/$processName"

        ant.exec(executable: "chmod", osfamily: "unix") {
            arg(value:  "--recursive")
            arg(value: "777")
            arg(value: "$deploymentDirPath/$processName")
        }

        ant.sequential() {
            ant.waitfor(maxwait: timeoutInSeconds, maxwaitunit: "second") {
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


    @Override
    public String toString() {
        return "OdeDeployer{" +
                "packageFilePath='" + packageFilePath + '\'' +
                ", deploymentDirPath='" + deploymentDirPath + '\'' +
                ", logFilePath='" + logFilePath + '\'' +
                ", processName='" + processName + '\'' +
                ", timeoutInSeconds=" + timeoutInSeconds +
                '}';
    }
}

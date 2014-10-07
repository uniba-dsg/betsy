package betsy.bpmn.engines.camunda

import ant.tasks.AntUtil

import java.nio.file.Path

class CamundaDeployer {
    private static final AntBuilder ant = AntUtil.builder()

    Path deploymentDirPath
    String processName
    Path packageFilePath

    public void deploy() {
        ant.copy(file: packageFilePath, todir: deploymentDirPath)
    }


    @Override
    public String toString() {
        return "CamundaDeployer{" +
                "deploymentDirPath='" + deploymentDirPath + '\'' +
                ", processName='" + processName + '\'' +
                ", packageFilePath='" + packageFilePath + '\'' +
                '}';
    }
}

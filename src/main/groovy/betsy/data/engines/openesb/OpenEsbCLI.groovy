package betsy.data.engines.openesb

import ant.tasks.AntUtil

class OpenEsbCLI {

    AntBuilder ant = AntUtil.builder()

    String glassfishHome

    void stopDomain() {
        ant.exec(executable: "cmd", failOnError: "true") {
            arg(value: "/c")
            arg(value: asAdmin)
            arg(value: "stop-domain")
            arg(value: "domain1")
        }
    }

    void startDomain() {
        ant.exec(executable: "cmd", failOnError: "true") {
            arg(value: "/c")
            arg(value: asAdmin)
            arg(value: "start-domain")
            arg(value: "domain1")
        }
    }

    private String getAsAdmin() {
        new File("${glassfishHome}/bin").absolutePath + "\\asadmin.bat"
    }

    void forceRedeploy(String processName, String packageFilePath, String tmpFolder) {

        String deployCommands = tmpFolder + "/deploy_commands.txt"

        ant.sequential() {
            ant.echo(message: """deploy-jbi-service-assembly ${packageFilePath}
start-jbi-service-assembly ${processName}Application""", file: deployCommands)

            ant.exec(executable: "cmd") {
                arg(value: "/c")
                arg(value: asAdmin)
                arg(value: "multimode")
                arg(value: "--file")
                arg(value: new File(deployCommands).absolutePath)
            }
        }
    }
}

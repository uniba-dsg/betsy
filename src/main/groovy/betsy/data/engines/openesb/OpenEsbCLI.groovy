package betsy.data.engines.openesb

import ant.tasks.AntUtil

import java.nio.file.Path

class OpenEsbCLI {

    AntBuilder ant = AntUtil.builder()

    Path glassfishHome

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

    private Path getAsAdmin() {
        glassfishHome.resolve("bin").resolve("asadmin.bat").toAbsolutePath()
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

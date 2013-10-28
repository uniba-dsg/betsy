package betsy.data.engines.openesb

import betsy.data.BetsyProcess


class OpenEsbCLI {

    AntBuilder ant = new AntBuilder()

    String serverPath

    void stopDomain(){
        ant.exec(executable: "cmd", failOnError: "true") {
            arg(value: "/c")
            arg(value: cliToolPath)
            arg(value: "stop-domain")
            arg(value: "domain1")
        }
    }

    void startDomain(){
        ant.exec(executable: "cmd", failOnError: "true") {
            arg(value: "/c")
            arg(value: cliToolPath)
            arg(value: "start-domain")
            arg(value: "domain1")
        }
    }

    private String getCliToolPath() {
        new File("${serverPath}/glassfish/bin").absolutePath + "\\asadmin.bat"
    }

    void forceRedeploy(BetsyProcess process) {

        String deployCommands = process.targetPath + "/deploy_commands.txt"

        ant.sequential() {
            ant.echo(message: """deploy-jbi-service-assembly ${process.targetPackageCompositeFilePath}
start-jbi-service-assembly ${process.bpelFileNameWithoutExtension}Application""", file: deployCommands)

            ant.exec(executable: "cmd") {
                arg(value: "/c")
                arg(value: cliToolPath)
                arg(value: "multimode")
                arg(value: "--file")
                arg(value: new File(deployCommands).absolutePath)
            }
        }
    }
}

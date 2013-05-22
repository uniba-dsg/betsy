package betsy.executables

import betsy.data.Engine
import betsy.data.Process

class CompositeSequential extends Composite {

    protected void executeEngine(Engine engine) {
        log "${context.testSuite.path}/${engine.name}", {
            for(Process process : engine.processes) {

                println "Process ${engine.processes.indexOf(process) + 1} of ${engine.processes.size()}"

                log "${process.targetPath}/all", {
                    try {
                        buildPackageAndTest(process)
                        installAndStart(engine)

                        log "${process.targetPath}/deploy", {
                            ant.echo message: "Deploying process ${process} to engine ${this}"
                            engine.deploy(process)
                            engine.onPostDeployment(process)
                        }

                        test(process)
                    } finally {
                        // ensure shutdown
                        log "${engine.path}/shutdown", {
                            engine.shutdown()
                        }
                    }
                }
            }
        }
    }

}

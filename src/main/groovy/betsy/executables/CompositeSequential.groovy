package betsy.executables

import betsy.data.Engine
import betsy.executables.generator.TestBuilder
import betsy.executables.soapui.runner.SoapUiRunner

class CompositeSequential extends Composite {

    protected void executeEngine(Engine engine) {
        log "${context.testSuite.path}/${engine.name}", {
            engine.processes.each { process ->

                println "Process ${engine.processes.indexOf(process) + 1} of ${engine.processes.size()}"

                log "${process.targetPath}/all", {
                    try {
                        // build
                        log "${process.targetPath}/build", {

                            log "${process.targetPath}/build_package", {
                                engine.buildArchives(process)
                            }

                            log "${process.targetPath}/build_test", {
                                soapui "${process.targetPath}/soapui_generation", {
                                    new TestBuilder(process: process,
                                            requestTimeout: context.requestTimeout,
                                            ant: ant).buildTest()
                                }
                            }
                        }

                        // setup infrastructure
                        log "${process.targetPath}/install", {
                            engine.install()
                        }

                        log "${process.targetPath}/startup", {
                            engine.startup()
                        }

                        log "${process.targetPath}/deploy", {
                            ant.echo message: "Deploying process ${process} to engine ${this}"
                            engine.deploy(process)
                            engine.onPostDeployment(process)
                        }

                        try {

                            try {
                                context.testPartner.publish()
                            } catch (BindException e){
                                context.testPartner.unpublish()
                                ant.echo "Address already in use - waiting 2 seconds to get available"
                                ant.sleep(milliseconds: 2000)
                                context.testPartner.publish()
                            }

                            log "${process.targetPath}/test", {
                                soapui "${process.targetPath}/soapui_test", {
                                    new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                                            reportingDirectory: process.targetReportsPath, ant: ant).run()
                                }
                                ant.sleep(milliseconds: 500)
                                engine.storeLogs(process)
                            }
                        } finally {
                            context.testPartner.unpublish()
                        }

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

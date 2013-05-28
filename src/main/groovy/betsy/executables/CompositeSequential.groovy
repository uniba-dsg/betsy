package betsy.executables

import betsy.data.engines.Engine;
import betsy.executables.generator.TestBuilder
import betsy.executables.soapui.runner.SoapUiRunner

class CompositeSequential extends Composite {

    protected void executeEngine(Engine engine) {
        log "${context.testSuite.path}/${engine.name}", {
            engine.processes.each { process ->
                new Retry(process: process, ant: ant).atMostThreeTimes {
                    String[] durationLog = ["-1", "-1", "-1", "-1", "-1", "-1", "-1"]

                    println "${engine.processes.indexOf(process) + 1} of ${engine.processes.size()}"
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
                            log "${process.targetPath}/engine_install", {
                                engine.install()
                            }

                            log "${process.targetPath}/engine_startup", {
                                engine.startup()
                            }

                            log "${process.targetPath}/deploy", {
                                ant.echo message: "Deploying process ${process} to engine ${this}"
                                engine.deploy(process)
                                engine.onPostDeployment(process)
                            }

                            context.testPartner.publish()
                            log "${process.targetPath}/test", {
                                soapui "${process.targetPath}/soapui_test", {
                                    new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                                            reportingDirectory: process.targetReportsPath, ant: ant).run()
                                }
                                ant.sleep(milliseconds: 500)
                                engine.storeLogs(process)
                            }
                            context.testPartner.unpublish()

                        } finally {
                            // ensure shutdown
                            log "${process.targetPath}/engine_shutdown", {
                                engine.shutdown()
                            }
                        }
                    }
                }
            }

        }
    }

}

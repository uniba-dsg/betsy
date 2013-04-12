package betsy.executables

import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TemporaryFailedTestException;
import betsy.data.engines.Engine;
import betsy.executables.generator.TestBuilder
import betsy.executables.soapui.runner.SoapUiRunner

class CompositeSequential extends Composite {

	protected void executeEngine(Engine engine) {
		log "${context.testSuite.path}/${engine.name}", {
			engine.processes.each { process ->

				boolean testProcess = true
				int testCount = 0;
				while(testProcess) {
					testProcess = false
					testCount++

					String[] durationLog = ["-1","-1","-1","-1","-1","-1","-1"]
					
					try {
						String repeat = testCount > 1 ? "Repeating process" : "Process"
						println "${repeat} ${engine.processes.indexOf(process) + 1} of ${engine.processes.size()}"

						durationLog[0] = log "${process.targetPath}/all", {
							try {
								// build
								durationLog[1] = log "${process.targetPath}/build", {

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
		                        durationLog[2] = log "${process.targetPath}/install", {
		                            engine.install()
		                        }
		
		                        durationLog[3] = log "${process.targetPath}/startup", {
		                            engine.startup()
		                        }

								durationLog[4] = log "${process.targetPath}/deploy", {
									ant.echo message: "Deploying process ${process} to engine ${this}"
									engine.deploy(process)
									engine.onPostDeployment(process)
								}

								context.testPartner.publish()
								durationLog[5] = log "${process.targetPath}/test", {
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
								durationLog[6] = log "${engine.path}/shutdown", {
		                            engine.shutdown()
		                        }
							}
						}
						
						// log durations
						ant.echo(message: "Total;Build;Installation;Startup;Deploy;Test;Shutdown\n", file: "${process.targetPath}/durations.csv", append: false, force: true)
						ant.echo(message: durationLog.join(";"), file: "${process.targetPath}/durations.csv", append: true, force: true)
					}catch(TemporaryFailedTestException exception) {
						println("Process ${engine.processes.indexOf(process) + 1} test failed")
						if(testCount <= 1) {
							testProcess = true
							// delete old log output by moving to failed tests

							def repeatedDir = "${process.engine.path}/failed_repeated_tests"
							def destDir = "${repeatedDir}/${testCount}_${process.normalizedId}"
							ant.mkdir(dir: repeatedDir)
							ant.move(file: process.targetPath, tofile: destDir, force: true, performGCOnFailedDelete: true)

						}else if(testCount > 1) {
							throw new IllegalStateException("Process " +
							"${engine.processes.indexOf(process) + 1} test " +
							"failed repeatedly: ", exception);
						}else {
							throw new IllegalStateException("Process " +
							"${engine.processes.indexOf(process) + 1} test " +
							"failed with a severe exception: ", exception);
						}
					}
				} // END WHILE
			}

		}
	}

}

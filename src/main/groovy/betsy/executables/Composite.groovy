package betsy.executables

import betsy.data.engines.Engine;
import betsy.data.BetsyProcess
import betsy.executables.analytics.Analyzer
import betsy.executables.soapui.builder.TestBuilder
import betsy.executables.reporting.Reporter
import soapui.SoapUiRunner
import betsy.executables.util.IOUtil
import betsy.executables.util.Stopwatch
import org.apache.log4j.FileAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout

class Composite {

	final AntBuilder ant = new AntBuilder()
	ExecutionContext context

	public void execute() {
		// use same ant builder in every class
		context.testSuite.ant = ant
		context.testSuite.engines.each { engine -> engine.ant = ant }
		context.testPartner.ant = ant

		// set output level to ERROR for console
		ant.project.getBuildListeners().get(0).setMessageOutputLevel(0)

		// prepare test suite
		// MUST BE OUTSITE OF LOG -> as it deletes whole file tree
		context.testSuite.prepare()

		log "${context.testSuite.path}/all", {

            try {
                // fail fast
                for(Engine engine : context.testSuite.engines){
                    engine.prepare()
                    engine.failIfRunning()
                }

                executeEngines(context)

                new Reporter(ant: ant, tests: context.testSuite).createReports()
                new Analyzer(ant: ant, csvFilePath: context.testSuite.csvFilePath,
                            reportsFolderPath: context.testSuite.reportsPath).createAnalytics()

			} catch (Exception e) {
				ant.echo message: IOUtil.getStackTrace(e), level: "error"
				throw e
			}

		}
	}

    protected void executeEngines(ExecutionContext context) {
        for(Engine engine : context.testSuite.engines){
            executeEngine(engine)
        }
	}

    protected void executeEngine(Engine engine) {
        log engine.path, {
            for(BetsyProcess process : engine.processes) {
                executeProcess(process)
            }
        }
    }

    protected void executeProcess(BetsyProcess process) {
        println "Process ${process.engine.processes.indexOf(process) + 1} of ${process.engine.processes.size()}"

        new Retry(process: process, ant: ant).atMostThreeTimes {
            log "${process.targetPath}/all", {
                try {
                    buildPackageAndTest(process)
                    installAndStart(process)
                    deploy(process)
                    test(process)
                } finally {
                    // ensure shutdown
                    shutdown(process)
                }
            }
        }
    }

    protected void shutdown(BetsyProcess process) {
        log "${process.targetPath}/engine_shutdown", {
            process.engine.shutdown()
        }
    }

    protected void deploy(BetsyProcess process) {
        log "${process.targetPath}/deploy", {
            ant.echo message: "Deploying process ${process} to engine ${process.engine}"
            process.engine.deploy(process)
            process.engine.onPostDeployment(process)
        }
    }

    protected void installAndStart(BetsyProcess process) {
        // setup infrastructure
        log "${process.targetPath}/engine_install", {
            process.engine.install()
        }

        log "${process.targetPath}/engine_startup", {
            process.engine.startup()
        }
    }

    protected void test(BetsyProcess process) {
        log "${process.targetPath}/test", {
            try {
                try {
                    context.testPartner.publish()
                } catch (BindException ignore) {
                    context.testPartner.unpublish()
                    ant.echo "Address already in use - waiting 2 seconds to get available"
                    ant.sleep(milliseconds: 2000)
                    context.testPartner.publish()
                }

                soapui "${process.targetPath}/test_soapui", {
                    new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                            reportingDirectory: process.targetReportsPath, ant: ant).run()
                }

                ant.sleep(milliseconds: 500)
                process.engine.storeLogs(process)

            } finally {
                context.testPartner.unpublish()
            }
        }
    }

    protected void buildPackageAndTest(BetsyProcess process) {
        log "${process.targetPath}/build", {

            log "${process.targetPath}/build_package", {
                String[] systemOuts = IOUtil.captureSystemOutAndErr { process.engine.buildArchives(process) }

                ant.echo message: "SoapUI System.out Output:\n\n${systemOuts[0]}"
                ant.echo message: "SoapUI System.err Output:\n\n${systemOuts[1]}"
            }

            log "${process.targetPath}/build_test", {
                soapui "${process.targetPath}/soapui_generation", {
                    new TestBuilder(process: process,
                            requestTimeout: context.requestTimeout,
                            ant: ant).buildTest()
                }
			}
		}
	}

	String log(String name, Closure closure) {
		try {
			ant.mkdir dir: new File(name).parent
			ant.record(name: name + ".log", action: "start", loglevel: "info", append: true)
	
			ant.echo message: name
			println name
	
			Stopwatch stopwatch = Stopwatch.benchmark(closure)
			String result = "${name} | ${stopwatch.formattedDiff} | (${stopwatch.diff}ms)"
			ant.echo message: result
			println result
		} finally {
			ant.record(name: name + ".log", action: "stop", loglevel: "info", append: true)
		}
	}

	void soapui(String name, Closure closure) {
		Logger root = Logger.getRootLogger();
		root.removeAllAppenders()
		root.setLevel(Level.INFO)
		root.addAppender(new FileAppender(
				new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), "${name}.log", true));

		String[] systemOuts = IOUtil.captureSystemOutAndErr closure

		ant.echo message: "SoapUI System.out Output:\n\n${systemOuts[0]}"
		ant.echo message: "SoapUI System.err Output:\n\n${systemOuts[1]}"
	}


}

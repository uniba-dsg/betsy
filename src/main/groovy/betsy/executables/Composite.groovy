package betsy.executables

import betsy.data.Engine
import betsy.data.Process
import betsy.executables.analytics.Analyzer
import betsy.executables.generator.TestBuilder
import betsy.executables.reporting.Reporter
import betsy.executables.soapui.runner.SoapUiRunner
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
                // create reports
                log "${context.testSuite.path}/prepare", {

                    // ensure folder structure
                    for(Engine engine : context.testSuite.engines){
                        engine.prepare()
                    }

                    // ensure that no engine is currently running
                    for(Engine engine : context.testSuite.engines){
                        engine.failIfRunning()
                    }
                }

                log "${context.testSuite.path}/execute", {
                    // prepare folder structure
                    executeEngines(context)
                }

                // create reports
                log "${context.testSuite.path}/report", {
                    new Reporter(ant: ant, tests: context.testSuite).createReports()
                }

                // create reports
                log "${context.testSuite.path}/analytics", {
                    new Analyzer(ant: ant, csvFilePath: context.testSuite.csvFilePath,
                            reportsFolderPath: context.testSuite.reportsPath).createAnalytics()
                }

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

        log "${context.testSuite.path}/${engine.name}", {
            try {
                // build
                log "${engine.path}/build", {
                    for(Process process : engine.processes) {
                        buildPackageAndTest(process)
                    }
                }

                installAndStart(engine)

                // deploy
                log "${engine.path}/deploy", {
                    for(Process process : engine.processes) {
                        log "${process.targetPath}/deploy", {
                            ant.echo message: "Deploying process ${process} to engine ${this}"
                            engine.deploy(process)
                        }
                    }
                    engine.onPostDeployment()
                }

                // test
                log "${engine.path}/test", {
                    for(Process process : engine.processes) {
                        test(process)
                    }
                }

            } finally {
                // ensure shutdown
                log "${engine.path}/shutdown", {
                    engine.shutdown()
                }
            }
        }
    }

    protected void installAndStart(Engine engine) {
        // setup infrastructure
        log "${engine.path}/install", {
            engine.install()
        }

        log "${engine.path}/startup", {
            engine.startup()
        }
    }

    protected void test(Process process) {
        try {
            try {
                context.testPartner.publish()
            } catch (BindException e) {
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
                process.engine.storeLogs(process)
            }
        } finally {
            context.testPartner.unpublish()
        }
    }

    protected void buildPackageAndTest(Process process) {
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

    void log(String name, Closure closure) {
        ant.mkdir dir: new File(name).parent
        ant.record(name: name + ".log", action: "start", loglevel: "info", append: true)

        ant.echo message: name
        println name

        String result = "${name} ${Stopwatch.benchmark(closure)}"
        ant.echo message: result
        println result

        ant.record(name: name + ".log", action: "stop", loglevel: "info", append: true)
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

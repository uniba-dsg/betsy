package betsy.executables

import betsy.data.Engine
import betsy.executables.generator.PackageBuilder
import betsy.executables.generator.TestBuilder
import betsy.executables.reporting.Reporter
import betsy.executables.soapui.SoapUiRunner
import betsy.executables.util.Stopwatch
import org.apache.log4j.FileAppender
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import betsy.executables.util.StringUtil

class Composite {

    final AntBuilder ant = new AntBuilder()
    ExecutionContext context

    public void execute() {
        context.testSuite.ant = ant
        context.testSuite.engines.each { engine -> engine.ant = ant}
        context.testPartner.ant = ant

        // set output level to ERROR for console
        ant.project.getBuildListeners().get(0).setMessageOutputLevel(0)

        // hack for downloading from site with bad ssl certificate
        new Init().allowInsecureDownloads()

        context.testSuite.prepare()

        // create reports
        log "${context.testSuite.path}/prepare", {
            // prepare folder structure
            context.testSuite.failIfAnyEngineIsRunning()
        }

        log "${context.testSuite.path}/execute", {
            // prepare folder structure
            executeEngines(context)
        }

        // create reports
        log "${context.testSuite.path}/report", {
            new Reporter(ant: ant, tests: context.testSuite).createReports()
        }
    }

    protected void executeEngines(ExecutionContext context) {
        context.testSuite.engines.each { engine ->
            executeEngine(engine)
        }
    }

    protected void executeEngine(Engine engine) {

        log "${context.testSuite.path}/${engine.name}", {
            try {
                // build
                log "${engine.path}/build", {
                    engine.processes.each {process ->
                        // deploy
                        log "${process.targetPath}/build", {

                            log "${process.targetPath}/build_package", {
                                new PackageBuilder(process: process, ant: ant).buildPackage()
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
                }

                // setup infrastructure
                log "${engine.path}/install", {
                    engine.install()
                }

                log "${engine.path}/startup", {
                    engine.startup()
                }

                // deploy
                log "${engine.path}/deploy", {
                    engine.processes.each {process ->
                        log "${process.targetPath}/deploy", {
                            ant.echo message: "Deploying process ${process} to engine ${this}"
                            engine.deploy(process)
                        }
                    }
                    engine.onPostDeployment()
                }

                // test
                log "${engine.path}/test", {
                    context.testPartner.publish()
                    engine.processes.each { process ->
                        log "${process.targetPath}/test", {
                            soapui "${process.targetPath}/soapui_test", {
                                new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                                        reportingDirectory: process.targetReportsPath, ant: ant).run()
                            }
                            ant.sleep(milliseconds: 500)
                            engine.storeLogs(process)
                        }
                    }
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

    def log = {String name, Closure closure ->
        ant.mkdir dir: new File(name).parent
        ant.record(name: name + ".log", action: "start", loglevel: "info", append: true)
        println "STARTING ${name}"
        println "DONE ${StringUtil.addSpaces(name,130)} --- ${benchmark(closure)}"
        ant.record(name: name + ".log", action: "stop", loglevel: "info", append: true)
    }

    def benchmark = { Closure closure ->
        Stopwatch stopwatch = new Stopwatch()
        stopwatch.start()
        closure.call()
        stopwatch.stop()
        ant.echo message: stopwatch.toString()

        stopwatch.formattedDiff
    }

    def soapui = { String name, Closure closure ->
        Logger root = Logger.getRootLogger();
        root.removeAllAppenders()
        root.setLevel(Level.INFO)
        root.addAppender(new FileAppender(
                new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN), "${name}.log", true));

        String[] systemOuts = captureSystemOutAndErr closure

        ant.echo message: "SoapUI System.out Output:\n\n${systemOuts[0]}"
        ant.echo message: "SoapUI System.err Output:\n\n${systemOuts[1]}"
    }

    def captureSystemOutAndErr = { Closure closure ->
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream()
        PrintStream newOut = new PrintStream(bufOut)
        PrintStream saveOut = System.out

        ByteArrayOutputStream bufErr = new ByteArrayOutputStream()
        PrintStream newErr = new PrintStream(bufErr)
        PrintStream saveOErr = System.err

        System.out = newOut
        System.err = newErr

        closure.call()

        System.out = saveOut
        System.err = saveOErr

        [bufOut.toString(), bufErr.toString()]
    }

}

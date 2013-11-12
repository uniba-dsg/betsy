package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.Reporter
import betsy.executables.soapui.builder.TestBuilder
import betsy.executables.util.IOUtil
import betsy.executables.util.Stopwatch
import betsy.logging.LogContext
import org.apache.log4j.Logger
import soapui.SoapUiRunner

class Composite {

    final AntBuilder ant = AntUtil.builder()

    private static Logger log = Logger.getLogger(Composite.class);

    ExecutionContext context

    public void execute() {

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        context.testSuite.prepare()

        log context.testSuite.path, {

            try {
                // fail fast
                for (Engine engine : context.testSuite.engines) {
                    engine.prepare()
                    engine.failIfRunning()
                }

                executeEngines(context)

                log context.testSuite.reportsPath, {
                    new Reporter(tests: context.testSuite).createReports()
                    new Analyzer(csvFilePath: context.testSuite.csvFilePath,
                            reportsFolderPath: context.testSuite.reportsPath).createAnalytics()
                }

            } catch (Exception e) {
                log.error e
                throw e
            }

        }
    }

    protected void executeEngines(ExecutionContext context) {
        for (Engine engine : context.testSuite.engines) {
            executeEngine(engine)
        }
    }

    protected void executeEngine(Engine engine) {
        log engine.path, {
            for (BetsyProcess process : engine.processes) {
                executeProcess(process)
            }
        }
    }

    protected void executeProcess(BetsyProcess process) {
        log.info "Process ${process.engine.processes.indexOf(process) + 1} of ${process.engine.processes.size()}"

        new Retry(process: process).atMostThreeTimes {
            log process.targetPath, {
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
            log.info "Deploying process ${process} to engine ${process.engine}"
            process.engine.deploy(process)
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
                    log.debug "Address already in use - waiting 2 seconds to get available"
                    ant.sleep(milliseconds: 2000)
                    context.testPartner.publish()
                }

                soapui "${process.targetPath}/test_soapui", {
                    new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                            reportingDirectory: process.targetReportsPath).run()
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

            soapui "${process.targetPath}/build_package", {
                process.engine.buildArchives(process)
            }

            soapui "${process.targetPath}/build_test", {
                new TestBuilder(process: process,
                        requestTimeout: context.requestTimeout).buildTest()
            }
        }
    }

    void log(String name, Closure closure) {
        String previous = LogContext.context;
        try {
            LogContext.context = name;

            ant.mkdir dir: new File(name).parent
            log.info "Start ..."
            Stopwatch stopwatch = Stopwatch.benchmark(closure)
            log.info "... finished in ${stopwatch.formattedDiff} | (${stopwatch.diff}ms)"
        } finally {
            LogContext.context = previous;
        }
    }

    void soapui(String name, Closure closure) {
        log name, {
            String[] systemOuts = IOUtil.captureSystemOutAndErr closure

            log.trace "SoapUI System.out Output:\n\n${systemOuts[0]}"
            log.trace "SoapUI System.err Output:\n\n${systemOuts[1]}"
        }
    }

}

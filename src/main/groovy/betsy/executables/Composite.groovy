package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.TestSuite
import betsy.data.engines.Engine
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.Reporter
import betsy.executables.soapui.builder.TestBuilder
import betsy.executables.util.Stopwatch
import betsy.logging.LogContext
import org.apache.log4j.Logger
import soapui.SoapUiRunner

import static betsy.executables.util.IOCapture.captureIO

class Composite {

    final static AntBuilder ant = AntUtil.builder()

    private static Logger log = Logger.getLogger(Composite.class);

    ExecutionContext context

    public void execute() {

        TestSuite testSuite = context.testSuite

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        testSuite.prepare()

        log testSuite.path, {

            // fail fast
            for (Engine engine : testSuite.engines) {
                engine.failIfRunning()
            }

            for (Engine engine : testSuite.engines) {
                log engine.path, {
                    engine.prepare()
                    for (BetsyProcess process : engine.processes) {
                        executeProcess(process)
                    }
                }
            }

            log testSuite.reportsPath, {
                new Reporter(tests: testSuite).createReports()
                new Analyzer(csvFilePath: testSuite.csvFilePath,
                        reportsFolderPath: testSuite.reportsPath).createAnalytics()
            }
        }

    }

    protected static void executeProcess(BetsyProcess process) {
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

    protected static void shutdown(BetsyProcess process) {
        log "${process.targetPath}/engine_shutdown", {
            process.engine.shutdown()
        }
    }

    protected static void deploy(BetsyProcess process) {
        log "${process.targetPath}/deploy", {
            log.info "Deploying process ${process} to engine ${process.engine}"
            process.engine.deploy(process)
        }
    }

    protected static void installAndStart(BetsyProcess process) {
        // setup infrastructure
        log "${process.targetPath}/engine_install", {
            process.engine.install()
        }

        log "${process.targetPath}/engine_startup", {
            process.engine.startup()
        }
    }

    protected static void test(BetsyProcess process) {
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

                log "${process.targetPath}/test_soapui", {
                    captureIO {
                        new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                                reportingDirectory: process.targetReportsPath).run()
                    }
                }

                ant.sleep(milliseconds: 500)
                process.engine.storeLogs(process)

            } finally {
                context.testPartner.unpublish()
            }
        }
    }

    protected static void buildPackageAndTest(BetsyProcess process) {
        log "${process.targetPath}/build", {

            log "${process.targetPath}/build_package", {
                captureIO { process.engine.buildArchives(process) }
            }

            log "${process.targetPath}/build_test", {
                captureIO { new TestBuilder(process: process, requestTimeout: context.requestTimeout).buildTest() }
            }
        }
    }

    private static log(String name, Closure closure) {
        String previous = LogContext.context;
        try {
            LogContext.context = name

            AntUtil.builder().mkdir dir: new File(name).parent
            log.info "Start ..."

            Stopwatch stopwatch = new Stopwatch()
            stopwatch.start()

            try {
                closure.call()
            } finally {
                stopwatch.stop()
                log.info "... finished in ${stopwatch.formattedDiff} | (${stopwatch.diff}ms)"
            }

        } finally {
            LogContext.context = previous
        }

    }

}

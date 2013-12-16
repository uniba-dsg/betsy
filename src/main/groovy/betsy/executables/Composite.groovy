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
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks
import org.apache.log4j.Logger
import org.apache.log4j.MDC
import soapui.SoapUiRunner

import java.nio.file.Path
import java.nio.file.Paths

import static betsy.executables.util.IOCapture.captureIO

class Composite {

    final static AntBuilder ant = AntUtil.builder()

    private static Logger logger = Logger.getLogger(Composite.class);

    ExecutionContext context

    public void execute() {

        TestSuite testSuite = context.testSuite

        Progress progress = new Progress(testSuite.processesCount)
        MDC.put("progress", progress.toString())

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        log testSuite.getPath(), {

            // fail fast
            for (Engine engine : testSuite.engines) {
                if(engine.isRunning()) {
                    throw new IllegalStateException("Engine $engine is running");
                }
            }

            for (Engine engine : testSuite.engines) {

                FileTasks.mkdirs(engine.getPath());

                log engine.path, {
                    for (BetsyProcess process : engine.processes) {

                        progress.next()
                        MDC.put("progress", progress.toString())

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

    protected void executeProcess(BetsyProcess process) {
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
                    logger.debug "Address already in use - waiting 2 seconds to get available"
                    WaitTasks.sleep(2000);
                    context.testPartner.publish()
                }

                log "${process.targetPath}/test_soapui", {
                    captureIO {
                        new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                                reportingDirectory: process.targetReportsPath).run()
                    }
                }

                WaitTasks.sleep(500);
                process.engine.storeLogs(process)

            } finally {
                context.testPartner.unpublish()
            }
        }
    }

    protected void buildPackageAndTest(BetsyProcess process) {
        log "${process.targetPath}/build", {

            buildPackage(process)

            buildTest(process)
        }
    }

    protected void buildTest(BetsyProcess process) {
        log "${process.targetPath}/build_test", {
            captureIO { new TestBuilder(process: process, requestTimeout: context.requestTimeout).buildTest() }
        }
    }

    protected void buildPackage(BetsyProcess process) {
        log "${process.targetPath}/build_package", {
            captureIO { process.engine.buildArchives(process) }
        }
    }

    protected static log(String name, Closure closure) {
        String previous = LogContext.context;
        try {
            LogContext.context = name

            FileTasks.mkdirs(Paths.get(name).toAbsolutePath().parent)
            logger.info "Start ..."

            Stopwatch stopwatch = new Stopwatch()
            stopwatch.start()

            try {
                closure.call()
            } finally {
                stopwatch.stop()
                logger.info "... finished in ${stopwatch.formattedDiff} | (${stopwatch.diff}ms)"
            }

        } finally {
            LogContext.context = previous
        }

    }

    protected static log(Path path, Closure closure) {
        log(path.toString(), closure)
    }

}

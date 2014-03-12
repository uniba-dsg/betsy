package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.BPMNReporter
import betsy.executables.util.Stopwatch
import betsy.logging.LogContext
import betsy.tasks.FileTasks
import org.apache.log4j.Logger
import org.apache.log4j.MDC

import java.nio.file.Path
import java.nio.file.Paths

import static betsy.executables.util.IOCapture.captureIO

class BPMNComposite {
    final static AntBuilder ant = AntUtil.builder()

    private static Logger logger = Logger.getLogger(BPMNComposite.class);

    BPMNTestSuite testSuite

    public void execute() {
        Progress progress = new Progress(testSuite.processesCount)
        MDC.put("progress", progress.toString())

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        log testSuite.getPath(), {
            // fail fast
            for (BPMNEngine engine : testSuite.engines) {
                if(engine.isRunning()) {
                    throw new IllegalStateException("Engine $engine is running");
                }
            }

            for (BPMNEngine engine : testSuite.engines) {

                FileTasks.mkdirs(engine.getPath());

                log engine.path, {
                    for (BPMNProcess process : engine.processes) {

                        progress.next()
                        MDC.put("progress", progress.toString())

                        executeProcess(process)
                    }
                }
            }

            createReports()
        }
    }

    protected createReports() {
        log testSuite.reportsPath, {
            new BPMNReporter(tests: testSuite).createReports()
            new Analyzer(csvFilePath: testSuite.csvFilePath,
                    reportsFolderPath: testSuite.reportsPath).createAnalytics()
        }
    }

    protected void executeProcess(BPMNProcess process) {
        //TODO Retry?
        log process.targetPath, {
            try {
                buildPackageAndTest(process)
                installAndStart(process)
                deploy(process)
                test(process)
                collect(process)
            } finally {
                // ensure shutdown
                shutdown(process)
            }
        }
    }

    protected void shutdown(BPMNProcess process) {
        log "${process.targetPath}/engine_shutdown", {
            process.engine.shutdown()
        }
    }

    protected void deploy(BPMNProcess process) {
        log "${process.targetPath}/deploy", {
            process.engine.deploy(process)
        }
    }

    protected void installAndStart(BPMNProcess process) {
        // setup infrastructure
        log "${process.targetPath}/engine_install", {
            process.engine.install()
        }

        log "${process.targetPath}/engine_startup", {
            process.engine.startup()
        }
    }

    protected void test(BPMNProcess process) {
        log "${process.targetPath}/test", {
            captureIO { process.engine.testProcess(process) }
        }
    }

    protected void collect(BPMNProcess process) {
        log "${process.targetPath}/collect", {
            process.engine.storeLogs(process)
        }
    }

    protected void buildPackageAndTest(BPMNProcess process) {
        log "${process.targetPath}/build", {

            buildPackage(process)

            buildTest(process)
        }
    }

    protected void buildTest(BPMNProcess process) {
        log "${process.targetPath}/build_test", {
            captureIO { process.engine.buildTest(process) }
        }
    }

    protected void buildPackage(BPMNProcess process) {
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
                new File("test/timings.csv").withWriterAppend { out -> out.println("${stopwatch.diff};${name};${name.replaceAll("\\\\","/").split("/").join(";")}") }
            }

        } finally {
            LogContext.context = previous
        }

    }

    protected static log(Path path, Closure closure) {
        log(path.toString(), closure)
    }
}
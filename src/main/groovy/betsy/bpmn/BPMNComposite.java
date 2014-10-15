package betsy.bpmn;

import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;
import betsy.bpmn.reporting.BPMNCsvReport;
import betsy.bpmn.reporting.BPMNReporter;
import betsy.common.analytics.Analyzer;
import betsy.common.tasks.FileTasks;
import betsy.common.util.IOCapture;
import betsy.common.util.LogUtil;
import betsy.common.util.Progress;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.nio.file.Path;

public class BPMNComposite {
    private static Logger logger = Logger.getLogger(BPMNComposite.class);
    private BPMNTestSuite testSuite;

    protected static void log(String name, Runnable closure) {
        LogUtil.log(name, logger, closure);
    }

    protected static void log(Path path, Runnable closure) {
        LogUtil.log(path, logger, closure);
    }

    public void execute() {
        final Progress progress = new Progress(testSuite.getProcessesCount());
        MDC.put("progress", progress.toString());

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        log(testSuite.getPath(), () -> {
            // fail fast
            for (BPMNEngine engine : testSuite.getEngines()) {
                if (engine.isRunning()) {
                    throw new IllegalStateException("Engine " + engine.getName() + " is running");
                }
            }

            for (BPMNEngine engine : testSuite.getEngines()) {

                FileTasks.mkdirs(engine.getPath());

                log(engine.getPath(), () -> {
                    for (BPMNProcess process : engine.getProcesses()) {

                        progress.next();
                        MDC.put("progress", progress.toString());

                        executeProcess(process);
                    }
                });
            }

            createReports();
        });
    }

    protected void createReports() {
        log(testSuite.getReportsPath(), () -> {
            new BPMNReporter(testSuite).createReports();
            new Analyzer(testSuite.getCsvFilePath(), testSuite.getReportsPath()).createAnalytics(new BPMNCsvReport());

        });
    }

    protected void executeProcess(final BPMNProcess process) {
        log(process.getTargetPath(), () -> {
            buildPackageAndTest(process);
            try {
                installAndStart(process);
                deploy(process);
                test(process);
                collect(process);
            } finally {
                // ensure shutdown
                shutdown(process);
            }
        });
    }

    protected void shutdown(final BPMNProcess process) {
        log(process.getTargetPath().resolve("engine_shutdown"), () -> process.getEngine().shutdown());
    }

    protected void deploy(final BPMNProcess process) {
        log(process.getTargetPath().resolve("deploy"), () -> process.getEngine().deploy(process));
    }

    protected void installAndStart(final BPMNProcess process) {
        // setup infrastructure
        log(process.getTargetPath().resolve("engine_install"), () -> process.getEngine().install());
        log(process.getTargetPath().resolve("engine_startup"), () -> process.getEngine().startup());
    }

    protected void test(final BPMNProcess process) {
        log(process.getTargetPath().resolve("test"), () -> process.getEngine().testProcess(process));
    }

    protected void collect(final BPMNProcess process) {
        log(process.getTargetPath().resolve("collect"), () -> process.getEngine().storeLogs(process));
    }

    protected void buildPackageAndTest(final BPMNProcess process) {
        log(process.getTargetPath().resolve("build"), () -> {
            buildPackage(process);
            buildTest(process);
        });
    }

    protected void buildTest(final BPMNProcess process) {
        log(process.getTargetPath().resolve("build_test"),
                () -> IOCapture.captureIO(() -> process.getEngine().buildTest(process)));
    }

    protected void buildPackage(final BPMNProcess process) {
        log(process.getTargetPath().resolve("build_package"),
                () -> IOCapture.captureIO(() -> process.getEngine().buildArchives(process)));
    }

    public BPMNTestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(BPMNTestSuite testSuite) {
        this.testSuite = testSuite;
    }
}

package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestSuite;
import betsy.bpel.reporting.BPELCsvReport;
import betsy.bpel.reporting.Reporter;
import betsy.bpel.soapui.TestBuilder;
import betsy.bpel.ws.DummyAndRegularTestPartnerService;
import betsy.bpel.ws.TestPartnerService;
import betsy.common.analytics.Analyzer;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.WaitTasks;
import betsy.common.util.IOCapture;
import betsy.common.util.LogUtil;
import betsy.common.util.Progress;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import soapui.SoapUiRunner;

import java.nio.file.Path;

public class BPELComposite {
    private static final Logger LOGGER = Logger.getLogger(BPELComposite.class);
    private TestPartnerService testPartner = new DummyAndRegularTestPartnerService();

    private BPELTestSuite testSuite;
    private int requestTimeout = 15000;

    protected static void log(String name, Runnable closure) {
        LogUtil.log(name, LOGGER, closure);
    }

    protected static void log(Path path, Runnable closure) {
        LogUtil.log(path, LOGGER, closure);
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
            for (AbstractBPELEngine engine : testSuite.getEngines()) {
                if (engine.isRunning()) {
                    throw new IllegalStateException("Engine " + engine + " is running");
                }
            }

            for (AbstractBPELEngine engine : testSuite.getEngines()) {
                FileTasks.mkdirs(engine.getPath());

                log(engine.getPath(), () -> {
                    for (BPELProcess process : engine.getProcesses()) {

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
            new Reporter(testSuite).createReports();
            new Analyzer(testSuite.getCsvFilePath(), testSuite.getReportsPath()).createAnalytics(new BPELCsvReport());
        });
    }

    protected void executeProcess(final BPELProcess process) {
        new Retry(process).atMostThreeTimes(() -> log(process.getTargetPath(), () -> {
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

        }));
    }

    protected void shutdown(final BPELProcess process) {
        log(process.getTargetPath() + "/engine_shutdown", () -> process.getEngine().shutdown());
    }

    protected void deploy(final BPELProcess process) {
        log(process.getTargetPath() + "/deploy", () -> process.getEngine().deploy(process));
    }

    protected void installAndStart(final BPELProcess process) {
        // setup infrastructure
        log(process.getTargetPath() + "/engine_install", () -> process.getEngine().install());
        log(process.getTargetPath() + "/engine_startup", () -> process.getEngine().startup());
    }

    protected void test(final BPELProcess process) {
        log(process.getTargetPath() + "/test", () -> {
            try {
                try {
                    testPartner.startup();
                } catch (Exception ignore) {
                    testPartner.shutdown();
                    LOGGER.debug("Address already in use - waiting 2 seconds to get available");
                    WaitTasks.sleep(2000);
                    testPartner.startup();
                }
                testSoapUi(process);
            } finally {
                testPartner.shutdown();
            }
        });
    }

    protected void collect(final BPELProcess process) {
        log(process.getTargetPath() + "/collect", () -> process.getEngine().storeLogs(process));
    }

    protected void testSoapUi(final BPELProcess process) {
        log(process.getTargetPath() + "/test_soapui", () -> IOCapture.captureIO(() ->
                new SoapUiRunner(process.getTargetSoapUIFilePath(), process.getTargetReportsPath()).run()));
        WaitTasks.sleep(500);
    }

    protected void buildPackageAndTest(final BPELProcess process) {
        log(process.getTargetPath() + "/build", () -> {
            buildPackage(process);
            buildTest(process);
        });
    }

    protected void buildTest(final BPELProcess process) {
        log(process.getTargetPath() + "/build_test", () ->
                IOCapture.captureIO(() -> new TestBuilder(process, requestTimeout).buildTest()));
    }

    protected void buildPackage(final BPELProcess process) {
        log(process.getTargetPath() + "/build_package",
                () -> IOCapture.captureIO(
                        () -> process.getEngine().buildArchives(process)));
    }

    public TestPartnerService getTestPartner() {
        return testPartner;
    }

    public void setTestPartner(TestPartnerService testPartner) {
        this.testPartner = testPartner;
    }

    public BPELTestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(BPELTestSuite testSuite) {
        this.testSuite = testSuite;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}

package betsy.bpmn;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;
import betsy.bpmn.reporting.BPMNCsvReport;
import betsy.bpmn.reporting.BPMNReporter;
import betsy.common.analytics.Analyzer;
import betsy.tools.TestsEngineDependent;
import betsy.common.tasks.FileTasks;
import betsy.common.util.IOCapture;
import betsy.common.util.LogUtil;
import betsy.common.util.Progress;
import betsy.tools.JsonMain;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.nio.file.Path;

import static betsy.common.config.Configuration.get;

public class BPMNComposite {
    private static final Logger LOGGER = Logger.getLogger(BPMNComposite.class);
    private BPMNTestSuite testSuite;

    private LogUtil logUtil;

    protected void log(String name, Runnable closure) {
        logUtil.log(name, LOGGER, closure);
    }

    protected void log(Path path, Runnable closure) {
        logUtil.log(path, LOGGER, closure);
    }

    public void execute() {
        final Progress progress = new Progress(testSuite.getProcessesCount());
        logUtil = new LogUtil(testSuite);
        MDC.put("progress", progress.toString());

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        log(testSuite.getPath(), () -> {

            // fail fast
            for (AbstractBPMNEngine engine : testSuite.getEngines()) {
                checkRunning(engine);
            }

            for (AbstractBPMNEngine engine : testSuite.getEngines()) {
                FileTasks.mkdirs(engine.getPath());
                log(engine.getPath(), () -> {
                    for (BPMNProcess process : engine.getProcesses()) {

                        progress.next();
                        MDC.put("progress", progress.toString());
                        try{
                            executeProcess(process);
                        } catch (Exception e) {
                            if(get("continue.on.exception").contains("true")){
                                Throwable cleanedException = StackTraceUtils.deepSanitize(e);
                                LOGGER.error("something went wrong during execution", cleanedException);
                            }else{
                                throw e;
                            }
                        }
                    }
                });
            }

            createReports();
        });
    }

    protected void checkRunning(AbstractBPMNEngine engine) {
        if (engine.isRunning()) {
            throw new IllegalStateException("EngineExtended " + engine.getName() + " is running");
        }
    }

    protected void createReports() {
        log(testSuite.getReportsPath(), () -> {
            new BPMNReporter(testSuite).createReports();
            new Analyzer(testSuite.getCsvFilePath(), testSuite.getReportsPath()).createAnalytics(new BPMNCsvReport());
            new TestsEngineDependent().createJson(testSuite);
            JsonMain.writeIntoSpecificFolder(testSuite.getPath());
        });
    }

    protected void executeProcess(final BPMNProcess process) {
        log(process.getTargetPath(), () -> {
            buildPackageAndTest(process);
            try {
                install(process);
                start(process);
                deploy(process);
                test(process);
            } catch (IllegalStateException e) {
                LOGGER.error("Test case " + process.getName() + " crashed", e);
            } finally {
                // always try to collect log files
                try {
                    collect(process);
                } catch(IllegalStateException ignore){
                    // not being able to store the logs should not crash the build
                }
                // ensure shutdown
                shutdown(process);
            }
        });
    }

    protected void shutdown(final BPMNProcess process) {
        log(process.getTargetPath().resolve("engine_shutdown"), () -> process.getEngine().shutdown());
    }

    protected void deploy(final BPMNProcess process) {
        log(process.getTargetPath().resolve("deploy"), () -> process.getEngine().deploy(process.getName(), process.getDeploymentPackagePath()));
    }

    protected void start(final BPMNProcess process) {
        log(process.getTargetPath().resolve("engine_startup"), () -> process.getEngine().startup());
    }

    protected void install(BPMNProcess process) {
        log(process.getTargetPath().resolve("engine_install"), () -> process.getEngine().install());
    }

    protected void test(final BPMNProcess process) {
        log(process.getTargetPath().resolve("test"), () -> process.getEngine().testProcess(process));
    }

    protected void collect(final BPMNProcess process) {
        log(process.getTargetPath().resolve("collect"), () -> process.getEngine().storeLogs(process.getTargetLogsPath()));
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
                () -> IOCapture.captureIO(() -> {
                    Path path = process.getEngine().buildArchives(process);
                    process.setDeploymentPackagePath(path);
                }));
    }

    public BPMNTestSuite getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(BPMNTestSuite testSuite) {
        this.testSuite = testSuite;
    }
}

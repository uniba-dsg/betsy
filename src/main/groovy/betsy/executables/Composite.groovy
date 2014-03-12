package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.data.TestSuite
import betsy.data.engines.Engine
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.Reporter
import betsy.executables.soapui.builder.TestBuilder
import betsy.executables.ws.TestPartnerService
import betsy.executables.ws.TestPartnerServicePublisher
import betsy.tasks.FileTasks
import betsy.tasks.WaitTasks
import org.apache.log4j.Logger
import org.apache.log4j.MDC
import soapui.SoapUiRunner

import static betsy.executables.util.IOCapture.captureIO
import static betsy.executables.util.LogUtil.log

class Composite {

    final static AntBuilder ant = AntUtil.builder()

    private static Logger logger = Logger.getLogger(Composite.class);

    TestPartnerService testPartner = new TestPartnerServicePublisher()
    TestSuite testSuite
    int requestTimeout = 15000

    public void execute() {
        Progress progress = new Progress(testSuite.processesCount)
        MDC.put("progress", progress.toString())

        // prepare test suite
        // MUST BE OUTSITE OF LOG -> as it deletes whole file tree
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        log testSuite.getPath(), logger, {

            // fail fast
            for (Engine engine : testSuite.engines) {
                if(engine.isRunning()) {
                    throw new IllegalStateException("Engine $engine is running");
                }
            }

            for (Engine engine : testSuite.engines) {

                FileTasks.mkdirs(engine.getPath());

                log engine.path, logger, {
                    for (BetsyProcess process : engine.processes) {

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
        log testSuite.reportsPath, logger, {
            new Reporter(tests: testSuite).createReports()
            new Analyzer(csvFilePath: testSuite.csvFilePath,
                    reportsFolderPath: testSuite.reportsPath).createAnalytics()
        }
    }

    protected void executeProcess(BetsyProcess process) {
        new Retry(process: process).atMostThreeTimes {
            log process.targetPath, logger, {
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
    }

    protected void shutdown(BetsyProcess process) {
        log "${process.targetPath}/engine_shutdown", logger, {
            process.engine.shutdown()
        }
    }

    protected void deploy(BetsyProcess process) {
        log "${process.targetPath}/deploy", logger, {
            process.engine.deploy(process)
        }
    }

    protected void installAndStart(BetsyProcess process) {
        // setup infrastructure
        log "${process.targetPath}/engine_install", logger, {
            process.engine.install()
        }

        log "${process.targetPath}/engine_startup", logger, {
            process.engine.startup()
        }
    }

    protected void test(BetsyProcess process) {
        log "${process.targetPath}/test", logger, {
            try {
                try {
                    testPartner.publish()
                } catch (BindException ignore) {
                    testPartner.unpublish()
                    logger.debug "Address already in use - waiting 2 seconds to get available"
                    WaitTasks.sleep(2000);
                    testPartner.publish()
                }

                testSoapUi(process)

            } finally {
                testPartner.unpublish()
            }
        }
    }

    protected void collect(BetsyProcess process) {
        log "${process.targetPath}/collect", logger, {
            process.engine.storeLogs(process)
        }
    }

    protected void testSoapUi(BetsyProcess process) {
        log "${process.targetPath}/test_soapui", logger, {
            captureIO {
                new SoapUiRunner(soapUiProjectFile: process.targetSoapUIFilePath,
                        reportingDirectory: process.targetReportsPath).run()
            }
        }

        WaitTasks.sleep(500);
    }

    protected void buildPackageAndTest(BetsyProcess process) {
        log "${process.targetPath}/build", logger, {

            buildPackage(process)

            buildTest(process)
        }
    }

    protected void buildTest(BetsyProcess process) {
        log "${process.targetPath}/build_test", logger, {
            captureIO { new TestBuilder(process: process, requestTimeout: requestTimeout).buildTest() }
        }
    }

    protected void buildPackage(BetsyProcess process) {
        log "${process.targetPath}/build_package", logger, {
            captureIO { process.engine.buildArchives(process) }
        }
    }

}

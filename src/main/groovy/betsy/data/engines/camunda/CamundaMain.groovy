package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.BetsyProcess
import betsy.data.TestSuite
import betsy.data.engines.Engine
import betsy.executables.Progress
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.Reporter
import betsy.tasks.FileTasks
import org.apache.log4j.MDC

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 25.02.14
 * Time: 10:13
 */
class CamundaMain {

    private static final AntBuilder ant = AntUtil.builder()

    public static void main(String[] args) {
        //setup testsuite
        CamundaEngine engine = new CamundaEngine(parentFolder: Paths.get("test"))
        List<Engine> engines = new ArrayList<>()
        engines.add(engine)
        BPMNProcess process = new BPMNProcess(name: "simple", group: "tasks", key: "SimpleApplication", groupId: "org.camunda.bpm.dsg", version: "0.0.1-SNAPSHOT")
        List<BPMNProcess> processes = new ArrayList<>()
        processes.add(process)
        BPMNTestSuite suite = BPMNTestSuite.createTests(engines, processes)

        // preparation
        Progress progress = new Progress(suite.processesCount)
        MDC.put("progress", progress.toString())
        FileTasks.deleteDirectory(suite.getPath());
        FileTasks.mkdirs(suite.getPath());
        FileTasks.mkdirs(suite.engines.first().getPath());
        FileTasks.mkdirs(suite.engines.first().processes.first().targetPath)
        FileTasks.mkdirs(suite.engines.first().processes.first().targetReportsPath)
        FileTasks.mkdirs(suite.engines.first().processes.first().targetTestBinPath)

        //engine installation and startup
        suite.engines.first().install()
        suite.engines.first().startup()

        //build and deploy process
        suite.engines.first().buildArchives(suite.engines.first().processes.first())
        suite.engines.first().deploy(suite.engines.first().processes.first())
        Thread.sleep(15000)

        // build and run test
        suite.engines.first().buildTest(suite.engines.first().processes.first())
        suite.engines.first().testProcess(suite.engines.first().processes.first())

        //collect
        suite.engines.first().storeLogs(suite.engines.first().processes.first())

        //generate reports
        /*Reporter reporter = new Reporter(tests: suite)
        reporter.createReports()
        new Analyzer(csvFilePath: suite.csvFilePath,
                reportsFolderPath: suite.reportsPath).createAnalytics()*/

        suite.engines.first().shutdown()

        //engine.isRunning()
    }
}

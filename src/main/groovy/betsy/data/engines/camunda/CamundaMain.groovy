package betsy.data.engines.camunda

import ant.tasks.AntUtil
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
        BetsyProcess process = new BetsyProcess(){
            public String getName() {"simple"}
            String getNormalizedId() {
                "tasks__simple"
            }
        }
        process.engine = engine
        engine.processes.add(process)
        TestSuite suite = new TestSuite(engines: engines, path: Paths.get("test"))

        // preparation
        Progress progress = new Progress(suite.processesCount)
        MDC.put("progress", progress.toString())
        FileTasks.deleteDirectory(suite.getPath());
        FileTasks.mkdirs(suite.getPath());
        FileTasks.mkdirs(engine.getPath());
        FileTasks.mkdirs(process.targetPath)
        FileTasks.mkdirs(process.targetReportsPath)
        FileTasks.mkdirs(process.targetPath.resolve("testBin"))

        //engine installation and startup
        engine.install()
        engine.startup()

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            http url: "http://localhost:8080"
        }

        //build and deploy process
        engine.buildArchives(process)
        engine.deploy(process)
        Thread.sleep(15000)

        // run test
        CamundaTester tester = new CamundaTester(restURL: "http://localhost:8080/engine-rest/engine/default",
                                                reportPath: process.targetReportsPath,
                                                testBin: process.targetPath.resolve("testBin"))
        tester.runTest()

        //collect
        /*FileTasks.mkdirs(process.targetLogsPath)
        ant.copy(todir: process.targetLogsPath) {
            ant.fileset(dir: "reports/test")
        }*/

        //generate reports
        Reporter reporter = new Reporter(tests: suite)
        reporter.createReports()
        new Analyzer(csvFilePath: suite.csvFilePath,
                reportsFolderPath: suite.reportsPath).createAnalytics()

        engine.shutdown()

        //engine.isRunning()
    }
}

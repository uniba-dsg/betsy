package betsy.data.engines.jbpm

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.BPMNReporter
import betsy.tasks.FileTasks

import java.nio.file.Paths

class JbpmMain {
    private static final AntBuilder ant = AntUtil.builder()

    public static void main(String[] args){
        List<BPMNEngine> engines = new ArrayList<>()
        JbpmEngine engine = new JbpmEngine(parentFolder: Paths.get("test"))
        engines.add(engine)
        List<BPMNProcess> processes = new ArrayList<>()
        BPMNProcess process = new BPMNProcess(name: "simple", key: "simple", group: "tasks", groupId: "testo", version: "1.0")
        processes.add(process)
        BPMNTestSuite testSuite = BPMNTestSuite.createTests(engines, processes)
        FileTasks.deleteDirectory(testSuite.getPath());
        FileTasks.mkdirs(testSuite.getPath());

        testSuite.engines.first().buildTest(testSuite.engines.first().processes.first())
        testSuite.engines.first().buildArchives(testSuite.engines.first().processes.first())
        try {
            testSuite.engines.first().install()
            testSuite.engines.first().startup()
            testSuite.engines.first().deploy(testSuite.engines.first().processes.first())
            testSuite.engines.first().testProcess(testSuite.engines.first().processes.first())

            new BPMNReporter(tests: testSuite).createReports()
            new Analyzer(csvFilePath: testSuite.csvFilePath,
                    reportsFolderPath: testSuite.reportsPath).createAnalytics()
            testSuite.engines.first().storeLogs(testSuite.engines.first().processes.first())
        }finally {
            engine.shutdown()
        }

        //engine.isRunning()
    }
}

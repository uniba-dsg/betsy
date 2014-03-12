package betsy.data.engines.camunda

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.data.engines.Engine
import betsy.executables.BPMNComposite
import betsy.executables.Progress
import betsy.executables.analytics.Analyzer
import betsy.executables.reporting.BPMNReporter
import betsy.tasks.FileTasks
import org.apache.log4j.MDC

import java.nio.file.Paths

class CamundaMain {

    private static final AntBuilder ant = AntUtil.builder()

    public static void main(String[] args) {
        //setup testsuite
        CamundaEngine engine = new CamundaEngine(parentFolder: Paths.get("test"))
        List<BPMNEngine> engines = new ArrayList<>()
        engines.add(engine)
        BPMNProcess process = new BPMNProcess(name: "XOR", group: "gateways", key: "XOR", groupId: "org.camunda.bpm.dsg", version: "1.0")
        List<BPMNProcess> processes = new ArrayList<>()
        processes.add(process)
        BPMNTestSuite suite = BPMNTestSuite.createTests(engines, processes)

        new BPMNComposite(testSuite: suite).execute()

    }
}

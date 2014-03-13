package betsy.data.engines.camunda

import betsy.data.BPMNProcess
import betsy.data.BPMNTestSuite
import betsy.data.engines.BPMNEngine
import betsy.executables.BPMNComposite

import java.nio.file.Paths

class CamundaMain {

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

package betsy.bpmn.model;

import betsy.bpmn.engines.BPMNEngine;
import betsy.common.model.TestSuite;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNTestSuite extends TestSuite<BPMNEngine, BPMNProcess> {
    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPMNTestSuite createTests(List<BPMNEngine> engines, final List<BPMNProcess> processes) {

        BPMNTestSuite test = new BPMNTestSuite();
        test.setPath(Paths.get("test"));

        for (BPMNEngine engine : engines) {
            List<BPMNProcess> clonedProcesses = processes.stream().map(BPMNProcess::createCopyWithoutEngine).collect(Collectors.toList());

            // link them
            for (BPMNProcess process : clonedProcesses) {
                process.setEngine(engine);
                engine.getProcesses().add(process);
            }

            // set parentFolder
            engine.setParentFolder(test.getPath());
        }

        test.setEngines(engines);
        test.setProcessesCount(getProcessesCount(engines));

        return test;
    }

    public static int getProcessesCount(List<BPMNEngine> engines) {
        int result = 0;

        for (BPMNEngine engine : engines) {
            result += engine.getProcesses().size();
        }

        return result;
    }
}

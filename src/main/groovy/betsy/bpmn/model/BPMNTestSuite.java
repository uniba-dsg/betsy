package betsy.bpmn.model;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.common.model.AbstractTestSuite;

public class BPMNTestSuite extends AbstractTestSuite<AbstractBPMNEngine, BPMNProcess> {

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @param testFolderName
     * @return a test suite where each engine tests all passed processes
     */
    public static BPMNTestSuite createTests(List<AbstractBPMNEngine> engines, final List<BPMNProcess> processes, String testFolderName) {

        BPMNTestSuite test = new BPMNTestSuite();
        test.setPath(Paths.get(testFolderName));

        for (AbstractBPMNEngine engine : engines) {
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

        Collections.shuffle(engines);
        for (AbstractBPMNEngine engine : engines) {
            Collections.shuffle(engine.getProcesses());
        }

        return test;
    }

    public static int getProcessesCount(List<AbstractBPMNEngine> engines) {
        int result = 0;

        for (AbstractBPMNEngine engine : engines) {
            result += engine.getProcesses().size();
        }

        return result;
    }
}

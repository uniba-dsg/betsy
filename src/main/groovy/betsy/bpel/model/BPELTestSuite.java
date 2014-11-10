package betsy.bpel.model;

import betsy.bpel.engines.AbstractEngine;
import betsy.common.model.TestSuite;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BPELTestSuite extends TestSuite<AbstractEngine, BPELProcess> {

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPELTestSuite createTests(List<AbstractEngine> engines, List<BPELProcess> processes) {
        BPELTestSuite test = new BPELTestSuite();
        test.setPath(Paths.get("test"));

        for (AbstractEngine engine : engines) {

            List<BPELProcess> clonedProcesses = processes.stream().map(BPELProcess::createCopyWithoutEngine).collect(Collectors.toList());

            // link them
            for (BPELProcess process : clonedProcesses) {
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

    public static int getProcessesCount(List<AbstractEngine> engines) {
        int result = 0;

        for (AbstractEngine engine : engines) {
            result += engine.getProcesses().size();
        }

        return result;
    }

}

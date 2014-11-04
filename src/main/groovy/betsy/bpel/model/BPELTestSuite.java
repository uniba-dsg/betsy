package betsy.bpel.model;

import betsy.bpel.engines.Engine;
import betsy.common.model.TestSuite;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BPELTestSuite extends TestSuite<Engine, BPELProcess> {

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPELTestSuite createTests(List<Engine> engines, List<BPELProcess> processes) {
        BPELTestSuite test = new BPELTestSuite();
        test.setPath(Paths.get("test"));

        for (Engine engine : engines) {

            List<BPELProcess> clonedProcesses = processes.stream().map(p -> (BPELProcess) p.clone()).collect(Collectors.toList());

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

    public static int getProcessesCount(List<Engine> engines) {
        int result = 0;

        for (Engine engine : engines) {
            result += engine.getProcesses().size();
        }

        return result;
    }

}

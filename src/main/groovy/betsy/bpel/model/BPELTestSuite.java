package betsy.bpel.model;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.common.model.AbstractTestSuite;

public class BPELTestSuite extends AbstractTestSuite<AbstractBPELEngine, BPELProcess> {

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @return a test suite where each engine tests all passed processes
     */
    public static BPELTestSuite createTests(List<AbstractBPELEngine> engines, List<BPELProcess> processes) {
        BPELTestSuite test = new BPELTestSuite();
        test.setPath(Paths.get("test"));

        for (AbstractBPELEngine engine : engines) {

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

        Collections.shuffle(engines);
        for (AbstractBPELEngine engine : engines) {
            Collections.shuffle(engine.getProcesses());
        }

        return test;
    }

    /**
     * Factory method for a list of engines and processes.
     *
     * @param engines   a list of engines to be included in the test suite
     * @param processes a list of processes to be included in the test suite
     * @param testFolderName a custom name for the test folder
     * @return a test suite where each engine tests all passed processes
     */
    public static BPELTestSuite createTests(List<AbstractBPELEngine> engines, List<BPELProcess> processes, String testFolderName) {
        BPELTestSuite test = new BPELTestSuite();
        test.setPath(Paths.get(testFolderName));

        for (AbstractBPELEngine engine : engines) {

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

        Collections.shuffle(engines);
        for (AbstractBPELEngine engine : engines) {
            Collections.shuffle(engine.getProcesses());
        }

        return test;
    }

    public static int getProcessesCount(List<AbstractBPELEngine> engines) {
        int result = 0;

        for (AbstractBPELEngine engine : engines) {
            result += engine.getProcesses().size();
        }

        return result;
    }

}

package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;
/**
 * Created by stssobetzko on 12.06.2015.
 */
public class PatternProcesses {

    public static final BPMNProcess SEQUENCE_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP01SequenceFlow", "A Process for a sequence flow with ",
            new BPMNTestCase().assertTask1());


    public static final BPMNProcess PARALLEL_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP02ParallelSplit", "This tests if all 3 ScriptTasks are written concurrently",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());
   
    public static final BPMNProcess SYNCHRONIZATION_PATTERN = BPMNProcessBuilder.buildPatternProcess("WCP03Synchronization", "Test for WCP03 the Synchronization Flow Pattern",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3().assertTask4());

    public static final List<BPMNProcess> PATTERNS = Arrays.asList(
            SEQUENCE_PATTERN,
			PARALLEL_PATTERN,
            SYNCHRONIZATION_PATTERN

    );

}

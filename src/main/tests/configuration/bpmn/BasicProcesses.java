package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

public class BasicProcesses {

    public static final BPMNProcess LANES = BPMNProcessBuilder.buildBasicProcess(
            "Lanes", "A collaboration with a single participant with two lanes. Lanes have no effect on the " +
                    "execution and should be ignored.",
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final BPMNProcess PARTICIPANT = BPMNProcessBuilder.buildBasicProcess(
            "Participant", "A collaboration with a single participant",
            new BPMNTestCase().assertTask1()
    );

    public static final BPMNProcess SEQUENCE_FLOW = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow", "A process with two scriptTasks connected by a sequenceFlow",
            new BPMNTestCase().assertTask1()
    );

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_Conditional", "A process with three scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other tasks with sequenceFlows. " +
                    "One of these sequenceFlows is associated with a conditionExpression",
            new BPMNTestCase().inputA().assertTask1().assertTask2(),
            new BPMNTestCase().inputB().assertTask2()
    );

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault", "A process with three scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other tasks with sequenceFlows. " +
                    "One of these sequenceFlows is associated with a conditionExpression, the other one is marked as default",
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2()
    );

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault_Normal", "A process with four scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other three tasks with sequenceFlows. " +
                    "The first of these sequenceFlows is associated with a conditionExpression, the second one is marked as default and the third has no condition associated. " +
                    "This is a special case document in Sec. 13.2.1, p. 427.",
//                    new BPMNTestCase(1).inputA().assertTask1().assertTask3(),
            new BPMNTestCase().inputB().assertTask2().assertTask3()
    );


    public static final List<BPMNProcess> BASICS = Arrays.asList(
            LANES,

            PARTICIPANT,

            SEQUENCE_FLOW,
            SEQUENCE_FLOW_CONDITIONAL,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL

    );
}

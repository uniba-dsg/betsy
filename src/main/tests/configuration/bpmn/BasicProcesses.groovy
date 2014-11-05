package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BasicProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess LANES = builder.buildBasicProcess(
            "Lanes", "A collaboration with a single participant with two lanes. Lanes have no effect on the " +
            "execution and should be ignored.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess PARTICIPANT = builder.buildBasicProcess(
            "Participant", "A collaboration with a single participant",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW = builder.buildBasicProcess(
            "SequenceFlow", "A process with two scriptTasks connected by a sequenceFlow",
            [
                    new BPMNTestCase().assertTask1()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL = builder.buildBasicProcess(
            "SequenceFlow_Conditional", "A process with three scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other tasks with sequenceFlows. " +
            "One of these sequenceFlows is associated with a conditionExpression",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT = builder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault", "A process with three scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other tasks with sequenceFlows. " +
            "One of these sequenceFlows is associated with a conditionExpression, the other one is marked as default",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL = builder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault_Normal", "A process with four scriptTasks connected by sequenceFlows. " +
            "The first scriptTask points to the other three tasks with sequenceFlows. " +
            "The first of these sequenceFlows is associated with a conditionExpression, the second one is marked as default and the third has no condition associated. " +
            "This is a special case document in Sec. 13.2.1, p. 427.",
            [
//                    new BPMNTestCase(1).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask2().assertTask3()
            ]
    )



    public static final List<BPMNProcess> BASICS = [
            LANES,

            PARTICIPANT,

            SEQUENCE_FLOW,
            SEQUENCE_FLOW_CONDITIONAL,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL

    ].flatten() as List<BPMNProcess>
}

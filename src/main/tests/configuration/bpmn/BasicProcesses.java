package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.feature.FeatureSet;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

public class BasicProcesses {

    public static final EngineIndependentProcess LANES = BPMNProcessBuilder.buildBasicProcess(
            "Lanes", "A collaboration with a single participant with two lanes. Lanes have no effect on the " +
                    "execution and should be ignored.",
            new Feature(new FeatureSet(Groups.BASICS, "Lanes", "A Lane is a sub-partition within a Process to organize and categorize Activities."), "Lanes"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3()
    );

    public static final EngineIndependentProcess PARTICIPANT = BPMNProcessBuilder.buildBasicProcess(
            "Participant", "A collaboration with a single participant",
            new Feature(new FeatureSet(Groups.BASICS, "Participant", "A Participant, depicted as a Pool in a BPMN diagram, represents a specific PartnerEntity (e.g., a company) "
                    + "and/or a more general PartnerRole (e.g., a buyer, seller, or manufacturer) that are participating in a Collaboration (see BPMN spec, p.113)."), "Participant"),
            new BPMNTestCase().assertTask1()
    );

    public static final EngineIndependentProcess SEQUENCE_FLOW = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow", "A process with two scriptTasks connected by a sequenceFlow",
            new Feature(new FeatureSet(Groups.BASICS, "SequenceFlow", "A SequenceFlow is used to define the order between elements in a Process."), "SequenceFlow"),
            new BPMNTestCase().assertTask1()
    );

    public static final FeatureSet CONSTRUCT_SEQUENCE_FLOW_CONDITIONAL = new FeatureSet(Groups.BASICS, "SequenceFlow_Conditional",
            "A conditional SequenceFlow is a SequenceFlow with an affixed condition.");
    public static final EngineIndependentProcess SEQUENCE_FLOW_CONDITIONAL = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_Conditional", "A process with three scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other tasks with sequenceFlows. " +
                    "One of these sequenceFlows is associated with a conditionExpression",
            new Feature(CONSTRUCT_SEQUENCE_FLOW_CONDITIONAL, "SequenceFlow_Conditional"),
            new BPMNTestCase().inputA().assertTask1().assertTask2(),
            new BPMNTestCase().inputB().assertTask2()
    );

    public static final EngineIndependentProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault", "A process with three scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other tasks with sequenceFlows. " +
                    "One of these sequenceFlows is associated with a conditionExpression, the other one is marked as default",
            new Feature(CONSTRUCT_SEQUENCE_FLOW_CONDITIONAL, "SequenceFlow_ConditionalDefault"),
            new BPMNTestCase().inputA().assertTask1(),
            new BPMNTestCase().inputB().assertTask2()
    );

    public static final EngineIndependentProcess SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL = BPMNProcessBuilder.buildBasicProcess(
            "SequenceFlow_ConditionalDefault_Normal", "A process with four scriptTasks connected by sequenceFlows. " +
                    "The first scriptTask points to the other three tasks with sequenceFlows. " +
                    "The first of these sequenceFlows is associated with a conditionExpression, the second one is marked as default and the third has no condition associated. " +
                    "This is a special case document in Sec. 13.2.1, p. 427.",
            new Feature(CONSTRUCT_SEQUENCE_FLOW_CONDITIONAL, "SequenceFlow_ConditionalDefault_Normal"),
//                    new BPMNTestCase(1).inputA().assertTask1().assertTask3(),
            new BPMNTestCase().inputB().assertTask2().assertTask3()
    );


    public static final List<EngineIndependentProcess> BASICS = Arrays.asList(
            LANES,

            PARTICIPANT,

            SEQUENCE_FLOW,
            SEQUENCE_FLOW_CONDITIONAL,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT,
            SEQUENCE_FLOW_CONDITIONAL_DEFAULT_NORMAL

    );
}

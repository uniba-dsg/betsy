package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

class GatewayProcesses {
    /*
    * GATEWAY CONSTRUCTS
    */
    public static final Construct CONSTRUCT_EXCLUSIVE_GATEWAY = new Construct(Groups.GATEWAYS, "ExclusiveGateway",
            "A diverging Exclusive Gateway (Decision) is used to create alternative paths within a Process flow."
                    + "A converging Exclusive Gateway is used to merge alternative paths. (see BPMN spec pp.289-291)");

    public static final Construct CONSTRUCT_EVENT_BASED_GATEWAY = new Construct(Groups.GATEWAYS, "EventBasedGateway",
            "The Event-Based Gateway represents a branching point in the Process where the alternative paths that follow"
                    + " the Gateway are based on Events that occur, rather than the evaluation of Expressions using "
                    + "Process data. (see BPMN spec pp.296-299)");

    public static final Construct CONSTRUCT_PARALLEL_GATEWAY = new Construct(Groups.GATEWAYS, "ParallelGateway",
            "A Parallel Gateway is used to synchronize (combine) parallel flows and to create parallel flows. (see "
                    + "BPMN spec pp.292-294)");

    public static final Construct CONSTRUCT_INCLUSIVE_GATEWAY = new Construct(Groups.GATEWAYS, "InclusiveGateway",
            "A diverging Inclusive Gateway (Inclusive Decision) can be used to create alternative but also parallel "
                    + "paths within a Process flow. A converging Inclusive Gateway is used to merge a combination of "
                    + "alternative and parallel paths. (see BPMN spec pp.291-292)");

    public static final Construct CONSTRUCT_COMPLEX_GATEWAY = new Construct(Groups.GATEWAYS, "ComplexGateway", "The "
            + "Complex Gateway can be used to model complex synchronization behavior not covered by other gateway types"
            + " (see BPMN spec pp.294-296)");

    public static final Construct CONSTRUCT_MIXED_GATEWAY_COMBINATIONS = new Construct(Groups.GATEWAYS, "MixedGatewayCombinations",
            "Testing combinations of different gateway types.");

    /*
     * FEATURE TESTS
     */

    public static final EngineIndependentProcess EXCLUSIVE_GATEWAY = BPMNProcessBuilder.buildGatewayProcess("ExclusiveGateway",
            "A process with four scriptTasks and exclusiveGateways. " +
                    "The execution of two of the tasks is controlled by the exclusiveGateways and only one of the tasks is actually executed.",
            new Feature(CONSTRUCT_EXCLUSIVE_GATEWAY, "ExclusiveGateway"),
            new BPMNTestCase().inputB().assertTask2().assertTask3(), new BPMNTestCase().inputA().assertTask1().assertTask3(), new BPMNTestCase().inputAB().assertTask1().assertTask3(), new BPMNTestCase().inputC().assertRuntimeException());

    public static final EngineIndependentProcess EXCLUSIVE_GATEWAY_DEFAULT = BPMNProcessBuilder.buildGatewayProcess("ExclusiveGateway_Default",
            "A process with five scriptTasks and exclusiveGateways. " +
                    "The execution of three of the tasks is controlled by the exclusiveGateways based on the input and only one of the tasks is actually executed." +
                    "Two tasks are triggered through sequenceFlows with conditionExpressions and one is triggered through a sequenceFlow which is marked as default.",
            new Feature(CONSTRUCT_EXCLUSIVE_GATEWAY, "ExclusiveGateway_Default"),
            new BPMNTestCase().inputB().assertTask2().assertTask4(), new BPMNTestCase().inputA().assertTask1().assertTask4(), new BPMNTestCase().inputAB().assertTask1().assertTask4(), new BPMNTestCase().inputC().assertTask3().assertTask4());

    public static final EngineIndependentProcess EXCLUSIVE_DIVERING_INCLUSIVE_CONVERGING = BPMNProcessBuilder.buildGatewayProcess("ExclusiveDiverging_InclusiveConverging",
            "A process with four scriptTasks, a diverging exclusiveGateway and a converging inclusiveGateway. " +
                    "Two of the tasks are enclosed between the gateways and only one of them is triggered depending on input data. " +
                    "The inclusiveGateway should merge the incoming branches.",
            new Feature(CONSTRUCT_MIXED_GATEWAY_COMBINATIONS, "ExclusiveDiverging_InclusiveConverging"),
            new BPMNTestCase().inputB().assertTask2().assertTask3(), new BPMNTestCase().inputA().assertTask1().assertTask3(), new BPMNTestCase().inputAB().assertTask1().assertTask3());

    public static final EngineIndependentProcess PARALLEL_GATEWAY = BPMNProcessBuilder.buildGatewayProcess("ParallelGateway",
            "A process with four scriptTasks and two parallelGateways. " +
                    "Two of the scriptTasks are surrounded by the parallelGateways.",
            new Feature(CONSTRUCT_PARALLEL_GATEWAY, "ParallelGateway"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());

    public static final EngineIndependentProcess INCLUSIVE_GATEWAY = BPMNProcessBuilder.buildGatewayProcess("InclusiveGateway",
            "A process with four scriptTasks, two of which are encapsulated by inclusiveGateways. " +
                    "Either one, none, or both of the scriptTasks are enabled based on input data.",
            new Feature(CONSTRUCT_INCLUSIVE_GATEWAY, "InclusiveGateway"),
            new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask3(), new BPMNTestCase().inputA().assertTask1().assertTask3(), new BPMNTestCase().inputB().assertTask2().assertTask3(), new BPMNTestCase().inputC().assertRuntimeException());

    public static final EngineIndependentProcess INCLUSIVE_GATEWAY_DEFAULT = BPMNProcessBuilder.buildGatewayProcess("InclusiveGateway_Default",
            "A process with five scriptTasks, three of which are encapsulated by inclusiveGateways. " +
                    "One of the scriptTasks acts as a default task. Either one, both of the others or the default task are executed based on input data.",
            new Feature(CONSTRUCT_INCLUSIVE_GATEWAY, "InclusiveGateway_Default"),
            new BPMNTestCase().inputC().assertTask3().assertTask4());

    public static final EngineIndependentProcess INCLUSIVE_DIVERGING_EXCLUSIVE_CONVERGING = BPMNProcessBuilder.buildGatewayProcess("InclusiveDiverging_ExclusiveConverging",
            "A process with four scriptTasks, a diverging inclusiveGateway and a converging exclusiveGateway. " +
                    "Two of the tasks are encapsulated by the gateways. " +
                    "Either one, none, or both of the scriptTasks are enabled based on input data and as a result the exclusiveGateway should either fire once or twice.",
            new Feature(CONSTRUCT_MIXED_GATEWAY_COMBINATIONS, "InclusiveDiverging_ExclusiveConverging"),
            new BPMNTestCase().inputAB().assertTask1().assertTask3().assertTask2().assertTask3(), new BPMNTestCase().inputA().assertTask1().assertTask3(), new BPMNTestCase().inputB().assertTask2().assertTask3());

    public static final EngineIndependentProcess PARALLEL_DIVERGING_EXCLUSIVE_CONVERGING = BPMNProcessBuilder.buildGatewayProcess("ParallelDiverging_ExclusiveConverging",
            "A process with four tasks, a diverging parallelGateway and a converging exclusiveGateway. " +
                    "Two of the tasks are executed in parallel and then merged by the exclusiveGateway. " +
                    "As a result, the task following the exclusiveGateway should be followed twice.",
            new Feature(CONSTRUCT_MIXED_GATEWAY_COMBINATIONS, "ParallelDiverging_ExclusiveConverging"),
            new BPMNTestCase().assertTask1().assertTask3().assertTask2().assertTask3());

    public static final EngineIndependentProcess PARALLEL_DIVERGING_INCLUSIVE_CONVERGING = BPMNProcessBuilder.buildGatewayProcess("ParallelDiverging_InclusiveConverging",
            "A process with four tasks, a diverging parallelGateway and a converging inclusiveGateway. " +
                    "Two of the tasks are executed in parallel and merged by the inclusiveGateway.",
            new Feature(CONSTRUCT_MIXED_GATEWAY_COMBINATIONS, "ParallelDiverging_InclusiveConverging"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask3());

    public static final EngineIndependentProcess PARALLEL_GATEWAY_TRUE_PARALLELISM = BPMNProcessBuilder.buildGatewayProcess("ParallelGateway_TrueParallelism",
            "A process with two scriptTasks between a diverging and a converging parallelGateway. " +
                    "The scriptTasks wait for some time, so their execution time intervals are expected to overlap.",
            new Feature(CONSTRUCT_PARALLEL_GATEWAY, "ParallelGateway_TrueParallelism"),
            new BPMNTestCase().assertExecutionParallel().optionDelay(22000));

    public static final EngineIndependentProcess EXCLUSIVE_GATEWAY_MIXED = BPMNProcessBuilder.buildGatewayProcess("ExclusiveGatewayMixed",
            "A process with six scriptTasks and three exclusiveGateways." +
                    "One of the gateways acts as a mixed gateway. Each pair of exclusiveGateways encapsulates two script tasks." +
                    "The enabling of these scriptTasks depends on input data",
            new Feature(CONSTRUCT_EXCLUSIVE_GATEWAY, "ExclusiveGatewayMixed"),
            new BPMNTestCase().inputA().assertTask2().assertTask4().assertTask5(), new BPMNTestCase().inputB().assertTask1().assertTask3().assertTask5(), new BPMNTestCase().inputAB().assertTask1().assertTask3().assertTask5());

    public static final EngineIndependentProcess COMPLEX_GATEWAY = BPMNProcessBuilder.buildGatewayProcess("ComplexGateway",
            "A process with five scriptTasks and two complexGateways. " +
                    "Three of the tasks are enclosed by the complexGateways and each one is enabled based on input data. " +
                    "The activationCondition of the converging complexGate is set to 'activationCount >= 1', so the gateway should fire for any number of activated incoming branches.",
            new Feature(CONSTRUCT_COMPLEX_GATEWAY, "ComplexGateway"),
            new BPMNTestCase().inputA().assertTask1().assertTask4(), new BPMNTestCase().inputB().assertTask2().assertTask4(), new BPMNTestCase().inputC().assertTask3().assertTask4(), new BPMNTestCase().inputAB().assertTask1().assertTask2().assertTask4());

    public static final EngineIndependentProcess EVENT_BASED_GATEWAY_SIGNALS = BPMNProcessBuilder.buildGatewayProcess("EventBasedGateway_Signals",
            "A process with five scriptTasks, a diverging parallelGateway, a diverging eventBasedGateway, an intermediate signal throw event and two intermediate signal catch events. " +
                    "The parallelGateway points to the eventBasedGateway in one branch and, in the other branch, throws the signal. " +
                    "This signal is caught by one of the branches following the eventBasedGateway.",
            new Feature(CONSTRUCT_EVENT_BASED_GATEWAY, "EventBasedGateway_Signals"),
            new BPMNTestCase().assertTask1().assertTask2().assertTask4().optionDelay(10000));

    public static final EngineIndependentProcess EVENT_BASED_GATEWAY_TIMER = BPMNProcessBuilder.buildGatewayProcess("EventBasedGateway_Timer",
            "A process with three scriptTasks, a diverging eventBasedGateway and two intermediate catch events. " +
                    "One of the catch events refers to a signal that is never thrown and the other one to a timer. " +
                    "Only the branch of the timer should ever be executed.",
            new Feature(CONSTRUCT_EVENT_BASED_GATEWAY, "EventBasedGateway_Timer"),
            new BPMNTestCase().assertTask2().optionDelay(5000));

    public static final List<EngineIndependentProcess> GATEWAYS = Arrays.asList(
            EXCLUSIVE_GATEWAY,
            EXCLUSIVE_GATEWAY_DEFAULT,
            EXCLUSIVE_GATEWAY_MIXED,
            EXCLUSIVE_DIVERING_INCLUSIVE_CONVERGING,

            INCLUSIVE_GATEWAY,
            INCLUSIVE_GATEWAY_DEFAULT,
            INCLUSIVE_DIVERGING_EXCLUSIVE_CONVERGING,

            PARALLEL_GATEWAY,
            PARALLEL_DIVERGING_EXCLUSIVE_CONVERGING,
            PARALLEL_DIVERGING_INCLUSIVE_CONVERGING,
            PARALLEL_GATEWAY_TRUE_PARALLELISM,

            COMPLEX_GATEWAY,

            EVENT_BASED_GATEWAY_SIGNALS,
            EVENT_BASED_GATEWAY_TIMER
    );
}

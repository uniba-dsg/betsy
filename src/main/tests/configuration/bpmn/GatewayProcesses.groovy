package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class GatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess EXCLUSIVE_GATEWAY = builder.buildGatewayProcess(
            "ExclusiveGateway", "A process with four scriptTasks and exclusiveGateways. " +
            "The execution of two of the tasks is controlled by the exclusiveGateways and only one of the tasks is actually executed.",
            [
                    new BPMNTestCase(2).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(1).inputB().assertTask2().assertTask3(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask3(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_GATEWAY_DEFAULT = builder.buildGatewayProcess(
            "ExclusiveGateway_Default", "A process with five scriptTasks and exclusiveGateways. " +
            "The execution of three of the tasks is controlled by the exclusiveGateways based on the input and only one of the tasks is actually executed." +
            "Two tasks are triggered through sequenceFlows with conditionExpressions and one is triggered through a sequenceFlow which is marked as default.",
            [
                    new BPMNTestCase(1).inputB().assertTask2().assertTask4(),
                    new BPMNTestCase(2).inputA().assertTask1().assertTask4(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask4(),
                    new BPMNTestCase(4).inputC().assertTask3().assertTask4()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_DIVERING_INCLUSIVE_CONVERGING = builder.buildGatewayProcess(
            "ExclusiveDiverging_InclusiveConverging", "A process with four scriptTasks, a diverging exclusiveGateway and a converging inclusiveGateway. " +
            "Two of the tasks are enclosed between the gateways and only one of them is triggered depending on input data. " +
            "The inclusiveGateway should merge the incoming branches.",
            [
                    new BPMNTestCase(2).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(1).inputB().assertTask2().assertTask3(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask3(),
            ]
    )

    public static final BPMNProcess PARALLEL_GATEWAY = builder.buildGatewayProcess(
            "ParallelGateway", "A process with four scriptTasks and two parallelGateways. " +
            "Two of the scriptTasks are surrounded by the parallelGateways.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess INCLUSIVE_GATEWAY = builder.buildGatewayProcess(
            "InclusiveGateway", "A process with four scriptTasks, two of which are encapsulated by inclusiveGateways. " +
            "Either one, none, or both of the scriptTasks are enabled based on input data.",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(2).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(3).inputB().assertTask2().assertTask3(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess INCLUSIVE_GATEWAY_DEFAULT = builder.buildGatewayProcess(
            "InclusiveGateway_Default", "A process with five scriptTasks, three of which are encapsulated by inclusiveGateways. " +
            "One of the scriptTasks acts as a default task. Either one, both of the others or the default task are executed based on input data.",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertTask4(),
                    new BPMNTestCase(2).inputA().assertTask1().assertTask4(),
                    new BPMNTestCase(3).inputB().assertTask2().assertTask4(),
                    new BPMNTestCase(4).inputC().assertTask3().assertTask4()
            ]
    )

    public static final BPMNProcess INCLUSIVE_DIVERGING_EXCLUSIVE_CONVERGING = builder.buildGatewayProcess(
            "InclusiveDiverging_ExclusiveConverging", "A process with four scriptTasks, a diverging inclusiveGateway and a converging exclusiveGateway. " +
            "Two of the tasks are encapsulated by the gateways. " +
            "Either one, none, or both of the scriptTasks are enabled based on input data and as a result the exclusiveGateway should either fire once or twice.",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask3().assertTask2().assertTask3(),
                    new BPMNTestCase(2).inputA().assertTask1().assertTask3(),
                    new BPMNTestCase(3).inputB().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess PARALLEL_DIVERGING_EXCLUSIVE_CONVERGING = builder.buildGatewayProcess(
            "ParallelDiverging_ExclusiveConverging", "A process with four tasks, a diverging parallelGateway and a converging exclusiveGateway. " +
            "Two of the tasks are executed in parallel and then merged by the exclusiveGateway. " +
            "As a result, the task following the exclusiveGateway should be followed twice.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask3().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess PARALLEL_DIVERGING_INCLUSIVE_CONVERGING = builder.buildGatewayProcess(
            "ParallelDiverging_InclusiveConverging", "A process with four tasks, a diverging parallelGateway and a converging inclusiveGateway. " +
            "Two of the tasks are executed in parallel and merged by the inclusiveGateway.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_GATEWAY_MIXED = builder.buildGatewayProcess(
            "ExclusiveGatewayMixed", "A process with six scriptTasks and three exclusiveGateways." +
            "One of the gateways acts as a mixed gateway. Each pair of exclusiveGateways encapsulates two script tasks." +
            "The enabling of these scriptTasks depends on input data",
            [
                    new BPMNTestCase(1).inputA().assertTask2().assertTask4().assertTask5(),
                    new BPMNTestCase(2).inputB().assertTask1().assertTask3().assertTask5(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask3().assertTask5(),
            ]
    )

    public static final BPMNProcess COMPLEX_GATEWAY = builder.buildGatewayProcess(
            "ComplexGateway", "A process with five scriptTasks and two complexGateways. " +
            "Three of the tasks are enclosed by the complexGateways and each one is enabled based on input data. " +
            "The activationCondition of the converging complexGate is set to 'activationCount >= 1', so the gateway should fire for any number of activated incoming branches.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask4(),
                    new BPMNTestCase(2).inputB().assertTask2().assertTask4(),
                    new BPMNTestCase(3).inputC().assertTask3().assertTask4(),
                    new BPMNTestCase(4).inputAB().assertTask1().assertTask2().assertTask4()
            ]
    )

    public static final BPMNProcess EVENT_BASED_GATEWAY_SIGNALS = builder.buildGatewayProcess(
            "EventBasedGateway_Signals", "A process with five scriptTasks, a diverging parallelGateway, a diverging eventBasedGateway, an intermediate signal throw event and two intermediate signal catch events. " +
            "The parallelGateway points to the eventBasedGateway in one branch and, in the other branch, throws the signal. " +
            "This signal is caught by one of the branches following the eventBasedGateway.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask4().optionDelay(10000)
            ]
    )

    public static final BPMNProcess EVENT_BASED_GATEWAY_TIMER = builder.buildGatewayProcess(
            "EventBasedGateway_Timer", "A process with three scriptTasks, a diverging eventBasedGateway and two intermediate catch events. " +
            "One of the catch events refers to a signal that is never thrown and the other one to a timer. " +
            "Only the branch of the timer should ever be executed.",
            [
                    new BPMNTestCase(1).assertTask2().optionDelay(5000)
            ]
    )

    public static final List<BPMNProcess> GATEWAYS = [
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

            COMPLEX_GATEWAY,

            EVENT_BASED_GATEWAY_SIGNALS,
            EVENT_BASED_GATEWAY_TIMER
    ].flatten() as List<BPMNProcess>
}

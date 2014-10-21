package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNGatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess EXCLUSIVE_GATEWAY = builder.buildGatewayProcess(
            "ExclusiveGateway", "A process with three scriptTasks and exclusiveGateways. " +
            "The execution of two of the tasks is controlled by the exclusiveGateways and only one of the tasks is actually executed.",
            [
                    new BPMNTestCase(1).inputB().assertFalse().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTrue().assertSuccess(),
                    new BPMNTestCase(3).inputAB().assertTrue().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_GATEWAY_DEFAULT = builder.buildGatewayProcess(
            "ExclusiveGatewayWithDefault", "A process with four scriptTasks and exclusiveGateways. " +
            "The execution of three of the tasks is controlled by the exclusiveGateways and only one of the tasks is actually executed." +
            "Two tasks are triggered through sequenceFlows with conditionExpressions and one is triggered through a sequenceFlow which is marked as default",
            [
                    new BPMNTestCase(1).inputB().assertFalse().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTrue().assertSuccess(),
                    new BPMNTestCase(3).inputAB().assertTrue().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertDefault().assertSuccess()
            ]
    )

    public static final BPMNProcess PARALLEL_GATEWAY = builder.buildGatewayProcess(
            "ParallelGateway", "A process with four scriptTasks and two parallelGateways. " +
            "Two of the scriptTasks are surrounded by the parallelGateways.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess PARALLEL_GATEWAY_CONDITION = builder.buildGatewayProcess(
            //TODO: this seems invalid
            "ParallelGatewayWithConditions", "A test for the parallel gateway to ignore conditions",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess INCLUSIVE_GATEWAY = builder.buildGatewayProcess(
            "InclusiveGateway", "Two scriptTasks encapsulated by inclusiveGateways. " +
            "Either one, none, or both of the scriptTasks are enabled based on input data.",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTask1().assertSuccess(),
                    new BPMNTestCase(3).inputB().assertTask2().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess INCLUSIVE_GATEWAY_DEFAULT = builder.buildGatewayProcess(
            "InclusiveGatewayWithDefault", "Three scriptTasks encapsulated by inclusiveGateways. " +
            "Either one, two, or one task marked as default, are enabled based on input data.",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTask1().assertSuccess(),
                    new BPMNTestCase(3).inputB().assertTask2().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertDefault().assertSuccess()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_EXCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInExclusiveOut", "Tests for the parallel gateway when joined with exclusive gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertSuccess().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_INCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInInclusiveOut", "Tests for the parallel gateway when joined with inclusive gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_IN_PARALLEL_OUT = builder.buildGatewayProcess(
            "ExclusiveInParallelOut", "Tests for the exclusive gateway when joined with parallel gateway",
            [
                    new BPMNTestCase(1).inputA().assertTrue(),
                    new BPMNTestCase(2).inputB().assertFalse(),
                    new BPMNTestCase(3).inputAB().assertTrue(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    /*
    * This process uses "\\empty"-scripts. We exclude it from execution until we know what to do about that
    *
    * public static final BPMNProcess EXCLUSIVE_MIXED = builder.buildGatewayProcess(
    *        "ExclusiveGatewayMixed", "Tests for the exclusive gateway when joined with parallel gateway",
    *        [
    *                new BPMNTestCase(1).inputA().assertTrue().assertSuccess(),
    *                new BPMNTestCase(2).inputB().assertFalse().assertSuccess()
    *        ]
    *)
    */

    public static final BPMNProcess COMPLEX = builder.buildGatewayProcess(
            "ComplexGateway", "Tests for the complex gateway",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess()
                    //this gateway is actually not supported by camunda and jbpm for that reason there are no more test cases
            ]
    )

    public static final BPMNProcess EVENT_BASED = builder.buildGatewayProcess(
            "EventBasedGateway", "Tests for the complex gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> GATEWAYS = [
            EXCLUSIVE_GATEWAY,
            EXCLUSIVE_GATEWAY_DEFAULT,
            PARALLEL_GATEWAY,
            PARALLEL_GATEWAY_CONDITION,
            INCLUSIVE_GATEWAY,
            INCLUSIVE_GATEWAY_DEFAULT,
            PARALLEL_IN_EXCLUSIVE_OUT,
            PARALLEL_IN_INCLUSIVE_OUT,
            EXCLUSIVE_IN_PARALLEL_OUT,
            //EXCLUSIVE_MIXED,
            COMPLEX,
            EVENT_BASED
    ].flatten() as List<BPMNProcess>
}

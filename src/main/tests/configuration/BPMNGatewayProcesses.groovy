package configuration

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNGatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess XOR = builder.buildGatewayProcess(
            "ExclusiveGateway", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway with testing the combination of the sequence flow conditions",
            [
                    new BPMNTestCase(1).inputB().assertFalse().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTrue().assertSuccess(),
                    new BPMNTestCase(3).inputAB().assertTrue().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess XOR_DEFAULT = builder.buildGatewayProcess(
            "ExclusiveGatewayWithDefault", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway with default sequence flow",
            [
                    new BPMNTestCase(1).inputB().assertFalse().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTrue().assertSuccess(),
                    new BPMNTestCase(3).inputAB().assertTrue().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertDefault().assertSuccess()
            ]
    )

    public static final BPMNProcess AND = builder.buildGatewayProcess(
            "ParallelGateway", "de.uniba.dsg", "1.0", "A simple test for the parallel gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess AND_CONDITION = builder.buildGatewayProcess(
            "ParallelGatewayWithConditions", "de.uniba.dsg", "1.0", "A test for the parallel gateway to ignore conditions",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess OR = builder.buildGatewayProcess(
            "InclusiveGateway", "de.uniba.dsg", "1.0", "Tests for the inclusive gateway with two sequence flows and their combination of conditions",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTask1().assertSuccess(),
                    new BPMNTestCase(3).inputB().assertTask2().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess OR_DEFAULT = builder.buildGatewayProcess(
            "InclusiveGatewayWithDefault", "de.uniba.dsg", "1.0", "Tests for the inclusive gateway with two conditioned sequence flows and a default sequence flow",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess(),
                    new BPMNTestCase(2).inputA().assertTask1().assertSuccess(),
                    new BPMNTestCase(3).inputB().assertTask2().assertSuccess(),
                    new BPMNTestCase(4).inputC().assertDefault().assertSuccess()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_EXCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInExclusiveOut", "de.uniba.dsg", "1.0", "Tests for the parallel gateway when joined with exclusive gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertSuccess().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_INCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInInclusiveOut", "de.uniba.dsg", "1.0", "Tests for the parallel gateway when joined with inclusive gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_IN_PARALLEL_OUT = builder.buildGatewayProcess(
            "ExclusiveInParallelOut", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway when joined with parallel gateway",
            [
                    new BPMNTestCase(1).inputA().assertTrue(),
                    new BPMNTestCase(2).inputB().assertFalse(),
                    new BPMNTestCase(3).inputAB().assertTrue(),
                    new BPMNTestCase(4).inputC().assertRuntimeException()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_MIXED = builder.buildGatewayProcess(
            "ExclusiveGatewayMixed", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway when joined with parallel gateway",
            [
                    new BPMNTestCase(1).inputA().assertTrue().assertSuccess(),
                    new BPMNTestCase(2).inputB().assertFalse().assertSuccess()
            ]
    )

    public static final BPMNProcess COMPLEX = builder.buildGatewayProcess(
            "ComplexGateway", "de.uniba.dsg", "1.0", "Tests for the complex gateway",
            [
                    new BPMNTestCase(1).inputAB().assertTask1().assertTask2().assertSuccess()
                    //this gateway is actually not supported by camunda and jbpm for that reason there are no more test cases
            ]
    )

    public static final BPMNProcess EVENT_BASED = builder.buildGatewayProcess(
            "EventBasedGateway", "de.uniba.dsg", "1.0", "Tests for the complex gateway",
            [
                    new BPMNTestCase(1).assertTask1().assertSuccess()
            ]
    )

    public static final List<BPMNProcess> GATEWAYS = [
            XOR,
            XOR_DEFAULT,
            AND,
            AND_CONDITION,
            OR,
            OR_DEFAULT,
            PARALLEL_IN_EXCLUSIVE_OUT,
            PARALLEL_IN_INCLUSIVE_OUT,
            EXCLUSIVE_IN_PARALLEL_OUT,
            EXCLUSIVE_MIXED,
            COMPLEX,
            EVENT_BASED
    ].flatten() as List<BPMNProcess>
}

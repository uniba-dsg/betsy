package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNGatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess XOR = builder.buildGatewayProcess(
            "ExclusiveGateway", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway with testing the combination of the sequence flow conditions",
            [
                    new BPMNTestCase(1).buildXorFalse(),
                    new BPMNTestCase(2).buildXorTrue(),
                    new BPMNTestCase(3).buildXorBothTrue(),
                    new BPMNTestCase(4).buildBothFalse()
            ]
    )

    public static final BPMNProcess XOR_DEFAULT = builder.buildGatewayProcess(
            "ExclusiveGatewayWithDefault", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway with default sequence flow",
            [
                    new BPMNTestCase(1).buildXorFalse(), //same as in XOR without default
                    new BPMNTestCase(2).buildXorTrue(),  //same as in XOR without default
                    new BPMNTestCase(3).buildXorBothTrue(),   //same as in XOR without default
                    new BPMNTestCase(4).buildXorWithDefaultBothFalse()
            ]
    )

    public static final BPMNProcess AND = builder.buildGatewayProcess(
            "ParallelGateway", "de.uniba.dsg", "1.0", "A simple test for the parallel gateway",
            [
                    new BPMNTestCase(1).buildAnd()
            ]
    )

    public static final BPMNProcess AND_CONDITION = builder.buildGatewayProcess(
            "ParallelGatewayWithConditions", "de.uniba.dsg", "1.0", "A test for the parallel gateway to ignore conditions",
            [
                    new BPMNTestCase(1).buildAnd()
            ]
    )

    public static final BPMNProcess OR = builder.buildGatewayProcess(
            "InclusiveGateway", "de.uniba.dsg", "1.0", "Tests for the inclusive gateway with two sequence flows and their combination of conditions",
            [
                    new BPMNTestCase(1).buildOrMultiFlow(),
                    new BPMNTestCase(2).buildOrSingleFlow1(),
                    new BPMNTestCase(3).buildOrSingleFlow2(),
                    new BPMNTestCase(4).buildBothFalse()
            ]
    )

    public static final BPMNProcess OR_DEFAULT = builder.buildGatewayProcess(
            "InclusiveGatewayWithDefault", "de.uniba.dsg", "1.0", "Tests for the inclusive gateway with two conditioned sequence flows and a default sequence flow",
            [
                    new BPMNTestCase(1).buildOrMultiFlow(),
                    new BPMNTestCase(2).buildOrSingleFlow1(),
                    new BPMNTestCase(3).buildOrSingleFlow2(),
                    new BPMNTestCase(4).buildDefault()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_EXCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInExclusiveOut", "de.uniba.dsg", "1.0", "Tests for the parallel gateway when joined with exclusive gateway",
            [
                    new BPMNTestCase(1).buildParallelInExclusiveOut()
            ]
    )

    public static final BPMNProcess PARALLEL_IN_INCLUSIVE_OUT = builder.buildGatewayProcess(
            "ParallelInInclusiveOut", "de.uniba.dsg", "1.0", "Tests for the parallel gateway when joined with inclusive gateway",
            [
                    new BPMNTestCase(1).buildAnd()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_IN_PARALLEL_OUT = builder.buildGatewayProcess(
            "ExclusiveInParallelOut", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway when joined with parallel gateway",
            [
                    new BPMNTestCase(1).buildExclusiveInParallelOutTrue(),
                    new BPMNTestCase(2).buildExclusiveInParallelOutFalse(),
                    new BPMNTestCase(3).buildExclusiveInParallelOutBothTrue(),
                    new BPMNTestCase(4).buildBothFalse()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_MIXED = builder.buildGatewayProcess(
            "ExclusiveGatewayMixed", "de.uniba.dsg", "1.0", "Tests for the exclusive gateway when joined with parallel gateway",
            [
                    new BPMNTestCase(1).buildXorTrue(),
                    new BPMNTestCase(2).buildXorFalse()
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
            EXCLUSIVE_MIXED
    ].flatten() as List<BPMNProcess>
}

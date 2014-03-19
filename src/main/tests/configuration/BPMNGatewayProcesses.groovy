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

    public static final List<BPMNProcess> GATEWAYS = [
            XOR,
            XOR_DEFAULT,
            AND,
            AND_CONDITION,
            OR,
            OR_DEFAULT,
            PARALLEL_IN_EXCLUSIVE_OUT
    ].flatten() as List<BPMNProcess>
}

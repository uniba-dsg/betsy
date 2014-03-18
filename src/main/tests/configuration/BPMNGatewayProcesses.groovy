package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNGatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess XOR = builder.buildGatewayProcess(
            "ExclusiveGateway", "de.uniba.dsg", "1.0", "A simple test for the exclusive gateway with testing both directions",
            [
                    new BPMNTestCase(1).buildXorFalse(),
                    new BPMNTestCase(2).buildXorTrue(),
                    new BPMNTestCase(3).buildXorBothTrue(),
                    new BPMNTestCase(4).buildXorBothFalse()
            ]
    )

    public static final BPMNProcess AND = builder.buildGatewayProcess(
            "ParallelGateway", "de.uniba.dsg", "1.0", "A simple test for the parallel gateway",
            [
                    new BPMNTestCase(1).buildAnd()
            ]
    )

    public static final BPMNProcess OR = builder.buildGatewayProcess(
            "InclusiveGateway", "de.uniba.dsg", "1.0", "A simple test for the inclusive gateway",
            [
                    new BPMNTestCase(1).buildOrMultiFlow(),
                    new BPMNTestCase(2).buildOrSingleFlow()
            ]
    )

    public static final List<BPMNProcess> GATEWAYS = [
            XOR,
            AND,
            OR
    ].flatten() as List<BPMNProcess>
}

package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNGatewayProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess XOR = builder.buildGatewayProcess(
            "XOR", "XOR", "org.camunda.bpm.dsg", "1.0", "A simple Test for the XOR Gateway",
            [
                    new BPMNTestCase().buildXorTrue().buildXorFalse()
            ]
    )

    public static final List<BPMNProcess> GATEWAYS = [
            XOR
    ].flatten() as List<BPMNProcess>
}

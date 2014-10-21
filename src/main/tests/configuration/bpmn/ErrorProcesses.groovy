package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

/**
 * This class bundles processes that are somewhat problematic, but engines should behave correctly by ignoring certain attributes or similar behavior
 */
class ErrorProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess PARALLEL_GATEWAY_WITH_CONDITIONS = builder.buildErrorProcess(
            "ParallelGatewayWithConditions", "A process with four scriptTasks and two parallelGateways. " +
            "Two of the scriptTasks are surrounded by the parallelGateways and the sequenceFlows pointing to the mergine gateway have conditions. " +
            "These conditions should be ignored by an engine.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(4).inputC().assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final List<BPMNProcess> ERRORS = [
            PARALLEL_GATEWAY_WITH_CONDITIONS,
    ].flatten() as List<BPMNProcess>
}

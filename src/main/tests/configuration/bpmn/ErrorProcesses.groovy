package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

/**
 * This class bundles processes that are somewhat problematic, but engines should behave correctly by ignoring certain attributes or similar behavior
 */
class ErrorProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess PARALLEL_GATEWAY_WITH_CONDITIONS = builder.buildErrorProcess(
            "ParallelGateway_Conditions", "A process with four scriptTasks and two parallelGateways. " +
            "Two of the scriptTasks are surrounded by the parallelGateways and the sequenceFlows pointing to the mergine gateway have conditions. " +
            "These conditions should be ignored by an engine.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(4).inputC().assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess EXCLUSIVE_DIVERGING_PARALLEL_CONVERGING = builder.buildErrorProcess(
            "ExclusiveDiverging_ParallelConverging", "A process with four scriptTasks, a diverging exclusiveGateway and a converging parallelGateway. " +
            "Two scriptTasks are enclosed by the gateways and the execution should deadlock, because only one incoming branch of the parallelGateway " +
            "should ever be executed. Hence, the scriptTask following the parallelGateway should never be executed.",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2(),
                    new BPMNTestCase(3).inputAB().assertTask1(),
            ]
    )

    public static final BPMNProcess INCLUSIVE_DIVERGING_PARALLEL_CONVERGING = builder.buildErrorProcess(
            "InclusiveDiverging_ParallelConverging", "A process with four scriptTasks, a diverging inclusiveGateway and a converging parallelGateway. " +
            "Two scriptTasks are enclosed by the gateways and the execution should deadlock if only one incoming branch of the parallelGateway " +
            "is enabled. Hence, the scriptTask following the parallelGateway should only be executed in a single case.",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2(),
                    new BPMNTestCase(3).inputAB().assertTask1().assertTask2().assertTask3(),
            ]
    )


    public static final List<BPMNProcess> ERRORS = [
            PARALLEL_GATEWAY_WITH_CONDITIONS,

            EXCLUSIVE_DIVERGING_PARALLEL_CONVERGING,
            INCLUSIVE_DIVERGING_PARALLEL_CONVERGING
    ].flatten() as List<BPMNProcess>
}

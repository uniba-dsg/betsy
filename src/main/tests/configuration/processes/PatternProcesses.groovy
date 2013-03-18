package configuration.processes

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation

/**
 * Created with IntelliJ IDEA.
 * User: joerg
 * Date: 18.03.13
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
class PatternProcesses {

    private Process buildPatternProcess(String name, List<TestCase> testCases) {
        new Process(bpel: "patterns/control-flow/${name}.bpel",
                wsdls: ["language-features/TestInterface.wsdl"],
                testCases: testCases
        )
    }

    public final Process SEQUENCE_PATTERN = buildPatternProcess(
            "SequencePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process PARALLEL_SPLIT_PATTERN = buildPatternProcess(
            "ParallelSplitPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process SYNCHRONIZATION_PATTERN = buildPatternProcess(
            "SynchronizationPattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1AB", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final Process EXCLUSIVE_CHOICE_PATTERN = buildPatternProcess(
            "ExclusiveChoicePattern",
            [
                    new TestCase(testSteps: [new TestStep(input: "1", stringOperationOutput: "1A", operation: WsdlOperation.SYNC_STRING)]),
                    new TestCase(testSteps: [new TestStep(input: "11", stringOperationOutput: "1B", operation: WsdlOperation.SYNC_STRING)])
            ]
    )

    public final List<Process> CONTROL_FLOW_PATTERNS = [
           SEQUENCE_PATTERN,
           PARALLEL_SPLIT_PATTERN,
           SYNCHRONIZATION_PATTERN,
           EXCLUSIVE_CHOICE_PATTERN
    ].flatten() as List<Process>


}

package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class EventProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess CANCEL = builder.buildEventProcess(
            "CancelEvent", "A simple test for canceling a transaction",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    // This test should be correct already
    public static final BPMNProcess COMPENSATION_END_EVENT_BOUNDARY_EVENT_SUB_PROCESS = builder.buildEventProcess(
            "CompensationEndEventBoundaryEventSubProcess", "A test with a subprocess which calls compensation on himself" +
            "as the SubProcess is not completed, the compensation has to be ignored.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask3()
            ]
    )

    /*
    // TODO Compensation tests need major rework
    public static final BPMNProcess COMPENSATION_BOUNDARY_THROW = builder.buildEventProcess(
            "CompensationBoundaryThrow", "A test for a compensation throw and a compensation boundary event",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_TASK_THROW = builder.buildEventProcess(
            "CompensationBoundaryTaskThrow", "A test for a compensation throw and a compensation boundary event on task level",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_TASK_END = builder.buildEventProcess(
            "CompensationBoundaryTaskEnd", "A test for a compensation end and a compensation boundary event on task level",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )

    public static final BPMNProcess COMPENSATION_EVENT_SUBPROCESS_END = builder.buildEventProcess(
            "CompensationEventSubprocessEnd", "A test for a compensation end and an event subprocess with a compensation start event",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )

    public static final BPMNProcess COMPENSATION_EVENT_SUBPROCESS_THROW = builder.buildEventProcess(
            "CompensationEventSubprocessThrow", "A test for a compensation throw and an event subprocess with a compensation start event",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )*/

    public static final BPMNProcess CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "ConditionalBoundaryEvent_SubProcess_Interrupting", "A test for an conditional boundary event attached to a" +
            "sub process which is marked as interrupting.",
            [
                    new BPMNTestCase(1).inputA().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "ConditionalBoundaryEvent_SubProcess_NonInterrupting", "A test for an conditional boundary event attached to a" +
            "sub process which is marked as non interrupting.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2().assertTask3(),
                    new BPMNTestCase(2).inputB().assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess CONDITIONAL_INTERMEDIATE_EVENT = builder.buildEventProcess(
            "ConditionalIntermediateEvent", "A test for an intermediate conditional event: ConditionIntermediate checks " +
            "a condition set at process instantiation. If the condition is fulfilled the process completes, if not the " +
            "process is locked at the event and should not complete.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2(),
                    new BPMNTestCase(2).inputB().assertTask1()
            ]
    )

    public static final BPMNProcess CONDITIONAL_START_EVENT = builder.buildEventProcess(
            "ConditionalStartEvent", "A test with a conditional start event",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB()
            ]
    )

    public static final BPMNProcess CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "ConditionalStartEvent_EventSubProcess_Interrupting", "A test for an interrupting conditional start event " +
            "in an event sub process",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "ConditionalStartEvent_EventSubProcess_NonInterrupting", "A test for an conditional start event in an event " +
            "sub process which is marked as non interrupting.",
            [
                    new BPMNTestCase(1).inputA().assertTask1().assertTask2(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess ERROR_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "ErrorBoundaryEvent_SubProcess_Interrupting", "A test for the error boundary event attached to a sub process." +
            "The task (task2) following the SequenceFlow originating from the boundary event should be executed. The Task (task3) " +
            "following the normal outgoing sequence flow after the SubProcess must not be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess ERROR_BOUNDARY_EVENT_TRANSACTION_INTERRUPTING = builder.buildEventProcess(
            "ErrorBoundaryEvent_Transaction_Interrupting", "A test for the error boundary event attached to a transaction." +
            "The task (task2) following the SequenceFlow originating from the boundary event should be executed. The Task (task3) " +
            "following the normal outgoing sequence flow after the Transaction must not be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess ERROR_END_EVENT_TOPLEVEL = builder.buildEventProcess(
            "ErrorEndEvent_TopLevel", "A simple test for the ErrorEndEvent in a top level process.",
            [
                    new BPMNTestCase(1).assertTask1().assertErrorThrownErrorEvent()
            ]
    )

    public static final BPMNProcess ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "ErrorStartEvent_EventSubProcess_Interrupting", "A test for the error start event in an event sub process. " +
            "After the execution of the EventSubProcess the flow should continue normally.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess ESCALATION_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "EscalationBoundaryEvent_SubProcess_Interrupting", "A test for an escalation event interrupting a subprocess." +
            "The task (task2) following the Intermediate EscalationEvent and the Task (task4) following the normal " +
            "outgoing sequence flow after the SubProcess must not be executed. " +
            "The SequenceFlow originating from the boundary event is activated and therefore Task3 should be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask3()
            ]
    )

    public static final BPMNProcess ESCALATION_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "EscalationBoundaryEvent_SubProcess_NonInterrupting", "A test for an escalation event NOT interrupting a subprocess." +
            "All tasks (Task1-4) should be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3().assertTask4()
            ]
    )

    public static final BPMNProcess ESCALATION_END_EVENT_SUBPROCESS = builder.buildEventProcess(
            "EscalationEndEvent_SubProcess", "A test for an escalation end event defined in a SubProcess." +
            "Only the task attached to the BoundaryEvent should be executed.",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    public static final BPMNProcess ESCALATION_END_EVENT_TOPLEVEL = builder.buildEventProcess(
            "EscalationEndEvent_TopLevel", "A test for an escalation end event in a top level process.",
            [
                    new BPMNTestCase(1).assertTask1().assertErrorThrownEscalationEvent()
            ]
    )

    public static final BPMNProcess ESCALATION_INTERMEDIATE_THROW_EVENT = builder.buildEventProcess(
            "EscalationIntermediateThrowEvent", "A test for an escalation intermediate throw event: " +
            "Task1 can only be executed if the event has been thrown (and caught).",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    public static final BPMNProcess ESCALATION_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "EscalationStartEvent_EventSubProcess_Interrupting", "A test for the interrupting escalation start event in " +
            "an event SubProcess. Task1 within in the (normal) SubProcess should not be executed. " +
            "After the execution of the EventSubProcess the flow should continue normally, and therefore Task3 should " +
            "be executed.",
            [
                    new BPMNTestCase(1).assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess ESCALATION_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "EscalationStartEvent_EventSubProcess_NonInterrupting", "A test for the escalation start event in an event " +
            "sub process which is marked as \"non interrupting\". Task2 within in the (normal) SubProcess and Task3 " +
            "which is defined after the SubProcess should be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3()
            ]
    )

    public static final BPMNProcess LINK = builder.buildEventProcess(
            "LinkEvent", "A simple test for link events",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    /*
     * Since we know no engine-independent way to send message, this is future work
    public static final BPMNProcess MESSAGE_START_EVENT = builder.buildEventProcess(
            "MessageStartEvent", "A test with a message start event",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )
    */


    //removed due to inconsistency in the bpmn standard about the use of a signal intermediate catch event (page 253 says no, the oversight on page 261 yes)
    /*public static final BPMNProcess SIGNAL_INTERMEDIATE = builder.buildEventProcess(
            "SignalIntermediateEvent", "A simple test for signal intermediate events",
            [
                    new BPMNTestCase(1).assertSuccess().assertSignaled()
            ]
    )*/

    public static final BPMNProcess SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "SignalBoundaryEvent_SubProcess_NonInterrupting", "A test for a signal boundary event NOT interrupting a subprocess." +
            "All tasks (Task1-4) should be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3().assertTask4()
            ]
    )

    public static final BPMNProcess SIGNAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "SignalBoundaryEvent_SubProcess_Interrupting", "A test for a signal boundary event interrupting a subprocess." +
            "The task (task2) following the Intermediate SignalEvent and the Task (task4) following the normal " +
            "outgoing sequence flow after the SubProcess must not be executed. " +
            "The SequenceFlow originating from the boundary event is activated and therefore Task3 should be executed.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask3()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocess", "A test with a subprocess with a signal intermediate throw event and a not interrupting signal start event",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled().assertNotInterrupted()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_1 = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocessInterrupting1", "A test with a subprocess with a signal intermediate throw event and an interrupting signal start event",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_2 = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocessInterrupting2", "A test with a subprocess with a signal intermediate throw event and an interrupting signal start event in a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled().assertSuccess()
            ]
    )

    /*
    * Until we have no way to actually send a starting signal, this test is of no use.
    * The engines will simply ignore the signal and start the process
    public static final BPMNProcess SIGNAL_START_EVENT = builder.buildEventProcess(
            "SignalStartEvent", "A test with a signal start event",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )
    */

    public static final BPMNProcess TERMINATE_EVENT = builder.buildEventProcess(
            "TerminateEvent", "A test for a terminate end event",
            [
                    new BPMNTestCase(1)
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_EVENT = builder.buildEventProcess(
            "TimerIntermediateEvent", "A process with two scriptTasks. There is a intermediateCatchEvent in between the tasks that delay the execution for a short period of time.",
            [
                    new BPMNTestCase(1).assertTask1().optionDelay(5000),
            ]
    )

    public static final BPMNProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "TimerBoundaryEvent_SubProcess_NonInterrupting", "A process with multiple scriptTasks and a subProcess with timer events. " +
            "The execution of the subProcess is delayed by an intermediate timer event for a short amount of time." +
            "In the meantime, a boundary timer event should fire without interrupting the subProcess.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3().optionDelay(5000),
            ]
    )

    public static final BPMNProcess TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "TimerBoundaryEvent_SubProcess_Interrupting", "A process with multiple scriptTasks and a subProcess with timer events. " +
            "The execution of the subProcess is delayed by an intermediate timer event for a short amount of time." +
            "In the meantime, a boundary timer event should fire and interrupt the subProcess.",
            [
                    new BPMNTestCase(1).assertTask3().optionDelay(5000),
            ]
    )

    //TODO: This might work with very long timeouts, still experimental
    public static final BPMNProcess TIMER_START_EVENT = builder.buildEventProcess(
            "TimerStartEvent", "A test with a timer start event",
            [
                    new BPMNTestCase(1).assertTask1().optionDelay(90000),
                    new BPMNTestCase(2)
            ]
    )


    public static final BPMNProcess TIMER_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING = builder.buildEventProcess(
            "TimerStartEvent_EventSubProcess_NonInterrupting", "A process with an ordinary subProcess and an event subProcess." +
            "The subProcess encloses the event subProcess and the latter is started by a timer startEvent. " +
            "All activities should be executed without interruption.",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertTask3().optionDelay(5000)
            ]
    )

    public static final BPMNProcess TIMER_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "TimerStartEvent_EventSubProcess_Interrupting", "A process with an ordinary subProcess and an event subProcess." +
            "The subProcess encloses the event subProcess and the latter is started by a timer startEvent. " +
            "The event subProcess interrupts the activities of its parent subProcess.",
            [
                    new BPMNTestCase(1).assertTask2().assertTask3().optionDelay(5000)
            ]
    )

    public static final List<BPMNProcess> EVENTS = [
            CANCEL,

            COMPENSATION_END_EVENT_BOUNDARY_EVENT_SUB_PROCESS,
//            COMPENSATION_BOUNDARY_THROW,
//            COMPENSATION_BOUNDARY_TASK_THROW,
//            COMPENSATION_BOUNDARY_TASK_END,
//            COMPENSATION_EVENT_SUBPROCESS_END,
//            COMPENSATION_EVENT_SUBPROCESS_THROW,

            CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            CONDITIONAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            CONDITIONAL_START_EVENT,
            CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            CONDITIONAL_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,
            CONDITIONAL_INTERMEDIATE_EVENT,

            ERROR_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            ERROR_BOUNDARY_EVENT_TRANSACTION_INTERRUPTING,
            ERROR_END_EVENT_TOPLEVEL,
            ERROR_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,

            ESCALATION_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            ESCALATION_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            ESCALATION_END_EVENT_SUBPROCESS,
            ESCALATION_END_EVENT_TOPLEVEL,
            ESCALATION_INTERMEDIATE_THROW_EVENT,
            ESCALATION_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            ESCALATION_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,

            LINK,

            //MESSAGE_START_EVENT,

            SIGNAL_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            SIGNAL_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_1,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_2,
            //SIGNAL_START_EVENT

            TERMINATE_EVENT,

            TIMER_INTERMEDIATE_EVENT,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_NON_INTERRUPTING,
            TIMER_BOUNDARY_EVENT_SUBPROCESS_INTERRUPTING,
            TIMER_START_EVENT_EVENT_SUBPROCESS_NON_INTERRUPTING,
            TIMER_START_EVENT_EVENT_SUBPROCESS_INTERRUPTING,
            TIMER_START_EVENT,
    ].flatten() as List<BPMNProcess>

}

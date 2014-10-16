package configuration.bpmn

import betsy.bpmn.model.BPMNProcess
import betsy.bpmn.model.BPMNTestCase

class BPMNEventProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess TIMER_INTERMEDIATE_EVENT = builder.buildEventProcess(
            "TimerIntermediateEvent", "Tests for the TimerIntermediateEvent",
            [
                    new BPMNTestCase(1).assertStarted().assertSuccess().optionDelay(5000),
                    new BPMNTestCase(2).assertStarted().optionDelay(1000)
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT = builder.buildEventProcess(
            "TimerIntermediateBoundaryEvent", "Tests for the timer intermediate boundary event",
            [
                    new BPMNTestCase(1).assertStarted().assertSuccess().assertTimerInternal().assertTimerExternal().optionDelay(5000),
                    new BPMNTestCase(2).assertStarted().optionDelay(100)
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_1 = builder.buildEventProcess(
            "TimerIntermediateBoundaryEventCancel1", "Tests for the timer intermediate boundary event canceling the activity",
            [
                    new BPMNTestCase(1).assertStarted().optionDelay(100),
                    new BPMNTestCase(2).assertStarted().assertTimerExternal().optionDelay(5000)
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_2 = builder.buildEventProcess(
            "TimerIntermediateBoundaryEventCancel2", "Tests for the timer intermediate boundary event canceling a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertStarted().optionDelay(100),
                    new BPMNTestCase(2).assertStarted().assertTimerExternal().optionDelay(5000)
            ]
    )

    public static final BPMNProcess TIMER_START_SUBPROCESS = builder.buildEventProcess(
            "TimerStartEventSubprocess", "Tests for the non interrupting timer start event of a event sub process",
            [
                    new BPMNTestCase(1).assertStarted().optionDelay(100),
                    new BPMNTestCase(2).assertStarted().assertTimerEvent().assertTimerInternal().assertSuccess().optionDelay(5000)
            ]
    )

    public static final BPMNProcess TIMER_START_SUBPROCESS_INTERRUPTING_1 = builder.buildEventProcess(
            "TimerStartEventSubprocessInterrupting1", "Tests for the interrupting timer start event of a event sub process",
            [
                    new BPMNTestCase(1).assertStarted().optionDelay(100),
                    new BPMNTestCase(2).assertStarted().assertTimerEvent().assertTimerInternal().optionDelay(5000)
            ]
    )

    public static final BPMNProcess TIMER_START_SUBPROCESS_INTERRUPTING_2 = builder.buildEventProcess(
            "TimerStartEventSubprocessInterrupting2", "Tests for the interrupting timer start event of a event sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertStarted().optionDelay(100),
                    new BPMNTestCase(2).assertStarted().assertTimerEvent().assertTimerInternal().assertSuccess().optionDelay(5000)
            ]
    )

    public static final BPMNProcess ERROR_END = builder.buildEventProcess(
            "ErrorEndEvent", "A simple test for the ErrorEndEvent",
            [
                    new BPMNTestCase(1).assertTask1().assertErrorThrownErrorEvent()
            ]
    )

    public static final BPMNProcess ERROR_INTERMEDIATE_1 = builder.buildEventProcess(
            "ErrorIntermediateEvent1", "A simple test for the error intermediate boundary event",
            [
                    new BPMNTestCase(1).assertSuccess().assertSubprocess()
            ]
    )

    public static final BPMNProcess ERROR_INTERMEDIATE_2 = builder.buildEventProcess(
            "ErrorIntermediateEvent2", "A simple test for the error intermediate boundary event on a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertSuccess().assertSubprocess()
            ]
    )

    public static final BPMNProcess ERROR_START_1 = builder.buildEventProcess(
            "ErrorStartEvent1", "A test for the error start event in an event triggered sub process",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess ERROR_START_2 = builder.buildEventProcess(
            "ErrorStartEvent2", "A test for the error start event in an event triggered sub process in a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2().assertSuccess()
            ]
    )

    public static final BPMNProcess ERROR_TRANSACTION = builder.buildEventProcess(
            "ErrorTransaction", "A test for the error end event and an error boundary event in a transaction sub process",
            [
                    new BPMNTestCase(1).assertTransactionTask().assertSuccess()
            ]
    )

    public static final BPMNProcess LINK = builder.buildEventProcess(
            "LinkEvent", "A simple test for link events",
            [
                    new BPMNTestCase(1).assertTask1()
            ]
    )

    //removed due to inconsistency in the bpmn standard about the use of a signal intermediate catch event (page 253 says no, the oversight on page 261 yes)
    /*public static final BPMNProcess SIGNAL_INTERMEDIATE = builder.buildEventProcess(
            "SignalIntermediateEvent", "A simple test for signal intermediate events",
            [
                    new BPMNTestCase(1).assertSuccess().assertSignaled()
            ]
    )*/

    public static final BPMNProcess SIGNAL_START_END = builder.buildEventProcess(
            "SignalStartEndEvent", "A test with 2 pools for signal start/end events",
            [
                    new BPMNTestCase(1).assertSuccess().assertSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_START = builder.buildEventProcess(
            "SignalIntermediateStartEvent", "A test with 2 pools for signal start/intermediate events",
            [
                    new BPMNTestCase(1).assertSuccess().assertSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY = builder.buildEventProcess(
            "SignalIntermediateBoundaryEvent", "A test with a subprocess with a signal end event and a not interrupting signal intermediate boundary event",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled().assertSuccess()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_1 = builder.buildEventProcess(
            "SignalIntermediateBoundaryEventCancel1", "A test with a subprocess with a signal end event and an interrupting signal intermediate boundary event",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_2 = builder.buildEventProcess(
            "SignalIntermediateBoundaryEventCancel2", "A test with a subprocess with a signal end event and an interrupting signal intermediate boundary event on a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).assertNormalTask().assertSignaled()
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

    public static final BPMNProcess SIGNAL_TWO_STARTS = builder.buildEventProcess(
            "SignalTwoStartEvents", "A test for signaling two signal start events in two pools",
            [
                    new BPMNTestCase(1).assertSignaled().assertSignaled().assertSuccess()
            ]
    )

    public static final BPMNProcess CANCEL = builder.buildEventProcess(
            "CancelEvent", "A simple test for canceling a transaction",
            [
                    new BPMNTestCase(1).assertTask1().assertTask2()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_END = builder.buildEventProcess(
            "CompensationBoundaryEnd", "A test for a compensation end and a compensation boundary event",
            [
                    new BPMNTestCase(1).assertSubprocess().assertCompensate().assertSuccess()
            ]
    )

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
    )

    //not supported either by camunda or jbpm
    public static final BPMNProcess ESCALATION_EVENT_SUBPROCESS_INTERRUPTING = builder.buildEventProcess(
            "EscalationEventSubprocessInterrupting", "A test for an escalation event interrupting a subprocess",
            [
                    new BPMNTestCase(1).assertTask1().assertInterrupted()
            ]
    )

    public static final BPMNProcess CONDITIONAL_START_SUBPROCESS_INTERRUPTING_1 = builder.buildEventProcess(
            "ConditionalStartEventSubprocessInterrupting1", "A test for an interrupting conditional start event in an event sub process",
            [
                    new BPMNTestCase(1).inputA().assertTask1(),
                    new BPMNTestCase(2).inputB().assertTask2()
            ]
    )

    public static final BPMNProcess CONDITIONAL_START_SUBPROCESS_INTERRUPTING_2 = builder.buildEventProcess(
            "ConditionalStartEventSubprocessInterrupting2", "A test for an interrupting conditional start event in an event sub process in a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).inputA().assertStarted().assertTask1().assertSuccess(),
                    new BPMNTestCase(2).inputB().assertStarted().assertNormalTask().assertSuccess()
            ]
    )

    public static final BPMNProcess CONDITIONAL_EVENT_INTERMEDIATE = builder.buildEventProcess(
            "ConditionalEventIntermediate", "A test for an intermediate conditional event",
            [
                    new BPMNTestCase(1).inputA().assertStarted().assertSuccess(),
                    new BPMNTestCase(2).inputB().assertStarted()
            ]
    )

    public static final BPMNProcess TERMINATE_EVENT = builder.buildEventProcess(
            "TerminateEvent", "A test for a terminate end event",
            [
                    new BPMNTestCase(1).assertStarted()
            ]
    )

    /*
    //It is not possible to create a signal engine independent and in this case the missing signal is ignored by the engines.
    //The process is wrongly started and for that simulates a correct result
    public static final BPMNProcess SIGNAL_START = builder.buildEventProcess(
            "SignalStartEvent", "A test with a signal start event",
            [
                    new BPMNTestCase(1).assertSuccess()
            ]
    )

    //The same behavior as for signal start event could be recognized for camunda here. jbpm complains about a missing message.
    public static final BPMNProcess MESSAGE_START = builder.buildEventProcess(
            "MessageStartEvent", "A test with a message start event",
            [
                    new BPMNTestCase(1).assertSuccess()
            ]
    )

    //Jbpm can not use the with the start transmitted variable 'test' as it needs an instantiated process for access to
    //the variables. Camunda does not support conditional and ignores it like in signal start event.
    public static final BPMNProcess CONDITIONAL_START = builder.buildEventProcess(
            "ConditionalStartEvent", "A test with a conditional start event",
            [
                    new BPMNTestCase(1).inputA().assertSuccess(),
                    new BPMNTestCase(2).inputB()
            ]
    )

    //Timer start event is not tested because guessing the time which is needed until the process really starts is environment specific
    public static final BPMNProcess TIMER_START = builder.buildEventProcess(
            "TimerStartEvent", "A test with a timer start event",
            [
                    new BPMNTestCase(1).assertSuccess().optionSelfStarting().optionDelay(5000),
                    new BPMNTestCase(2).optionSelfStarting()
            ]
    )
    */

    public static final List<BPMNProcess> EVENTS = [
            TIMER_INTERMEDIATE_EVENT,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_1,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_2,
            TIMER_START_SUBPROCESS,
            TIMER_START_SUBPROCESS_INTERRUPTING_1,
            TIMER_START_SUBPROCESS_INTERRUPTING_2,
            ERROR_END,
            ERROR_INTERMEDIATE_1,
            ERROR_INTERMEDIATE_2,
            ERROR_START_1,
            ERROR_START_2,
            ERROR_TRANSACTION,
            LINK,
            SIGNAL_INTERMEDIATE_BOUNDARY,
            SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_1,
            SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_2,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_1,
            SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_2,
            SIGNAL_START_END,
            SIGNAL_INTERMEDIATE_START,
            SIGNAL_TWO_STARTS,
            CANCEL,
            COMPENSATION_BOUNDARY_END,
            COMPENSATION_BOUNDARY_THROW,
            COMPENSATION_BOUNDARY_TASK_THROW,
            COMPENSATION_BOUNDARY_TASK_END,
            COMPENSATION_EVENT_SUBPROCESS_END,
            COMPENSATION_EVENT_SUBPROCESS_THROW,
            ESCALATION_EVENT_SUBPROCESS_INTERRUPTING,
            CONDITIONAL_EVENT_INTERMEDIATE,
            CONDITIONAL_START_SUBPROCESS_INTERRUPTING_1,
            CONDITIONAL_START_SUBPROCESS_INTERRUPTING_2,
            TERMINATE_EVENT,
            /*SIGNAL_START,
            MESSAGE_START,
            CONDITIONAL_START,
            TIMER_START*/
    ].flatten() as List<BPMNProcess>

}

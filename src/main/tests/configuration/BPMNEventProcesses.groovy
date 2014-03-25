package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNEventProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

//    public static final BPMNProcess TIMER_EVENT = builder.buildEventProcess(
//            "TimerStartEvent", "de.uniba.dsg", "1.0", "Tests for the TimerEvent",
//            [
//                    new BPMNTestCase(number: 1, selfStarting: true, delay: 5000).buildSimple(),
//                    new BPMNTestCase(number: 2, selfStarting: true, delay: 1000).buildSimple()
//            ]
//    )

    public static final BPMNProcess TIMER_INTERMEDIATE_EVENT = builder.buildEventProcess(
            "TimerIntermediateEvent", "de.uniba.dsg", "1.0", "Tests for the TimerIntermediateEvent",
            [
                    new BPMNTestCase(number: 1, selfStarting: false, delay: 5000).buildTimerIntermediateEventOnTime(),
                    new BPMNTestCase(number: 2, selfStarting: false, delay: 1000).buildTimerIntermediateEventNotOnTime()
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT = builder.buildEventProcess(
            "TimerIntermediateBoundaryEvent", "de.uniba.dsg", "1.0", "Tests for the timer intermediate boundary event",
            [
                    new BPMNTestCase(number: 1, selfStarting: false, delay: 5000).buildTimerBoundaryOnTime(),
                    new BPMNTestCase(number: 2, selfStarting: false, delay: 100).buildTimerIntermediateEventNotOnTime()
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_1 = builder.buildEventProcess(
            "TimerIntermediateBoundaryEventCancel1", "de.uniba.dsg", "1.0", "Tests for the timer intermediate boundary event canceling the activity",
            [
                    new BPMNTestCase(number: 1, selfStarting: false, delay: 100).buildTimerIntermediateEventNotOnTime(),
                    new BPMNTestCase(number: 2, selfStarting: false, delay: 5000).buildTimerBoundaryCancelingOnTime()
            ]
    )

    public static final BPMNProcess TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_2 = builder.buildEventProcess(
            "TimerIntermediateBoundaryEventCancel2", "de.uniba.dsg", "1.0", "Tests for the timer intermediate boundary event canceling a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(number: 1, selfStarting: false, delay: 100).buildTimerIntermediateEventNotOnTime(),
                    new BPMNTestCase(number: 2, selfStarting: false, delay: 5000).buildTimerBoundaryCancelingOnTime()
            ]
    )

    public static final BPMNProcess ERROR_END = builder.buildEventProcess(
            "ErrorEndEvent", "de.uniba.dsg", "1.0", "A simple test for the ErrorEndEvent",
            [
                    new BPMNTestCase(1).buildSimpleError()
            ]
    )

    public static final BPMNProcess ERROR_INTERMEDIATE_1 = builder.buildEventProcess(
            "ErrorIntermediateEvent1", "de.uniba.dsg", "1.0", "A simple test for the error intermediate boundary event",
            [
                    new BPMNTestCase(1).buildSubprocess()
            ]
    )

    public static final BPMNProcess ERROR_INTERMEDIATE_2 = builder.buildEventProcess(
            "ErrorIntermediateEvent2", "de.uniba.dsg", "1.0", "A simple test for the error intermediate boundary event on a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).buildSubprocess()
            ]
    )

    public static final BPMNProcess ERROR_START_1 = builder.buildEventProcess(
            "ErrorStartEvent1", "de.uniba.dsg", "1.0", "A test for the error start event in an event triggered sub process",
            [
                    new BPMNTestCase(1).buildErrorStartEvent1()
            ]
    )

    public static final BPMNProcess ERROR_START_2 = builder.buildEventProcess(
            "ErrorStartEvent2", "de.uniba.dsg", "1.0", "A test for the error start event in an event triggered sub process in a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).buildErrorStartEvent2()
            ]
    )

    public static final BPMNProcess ERROR_TRANSACTION = builder.buildEventProcess(
            "ErrorTransaction", "de.uniba.dsg", "1.0", "A test for the error end event and an error boundary event in a transaction sub process",
            [
                    new BPMNTestCase(1).buildTransaction()
            ]
    )

    public static final BPMNProcess LINK = builder.buildEventProcess(
            "LinkEvent", "de.uniba.dsg", "1.0", "A simple test for link events",
            [
                    new BPMNTestCase(1).buildSimple()
            ]
    )

    //removed due to inconsistency in the bpmn standard about the use of a signal intermediate catch event (page 253 says no, the oversight on page 261 yes)
    /*public static final BPMNProcess SIGNAL_INTERMEDIATE = builder.buildEventProcess(
            "SignalIntermediateEvent", "de.uniba.dsg", "1.0", "A simple test for signal intermediate events",
            [
                    new BPMNTestCase(1).buildSignaled()
            ]
    )*/

    public static final BPMNProcess SIGNAL_START_END = builder.buildEventProcess(
            "SignalStartEndEvent", "de.uniba.dsg", "1.0", "A test with 2 pools for signal start/end events",
            [
                    new BPMNTestCase(1).buildSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_START = builder.buildEventProcess(
            "SignalIntermediateStartEvent", "de.uniba.dsg", "1.0", "A test with 2 pools for signal start/intermediate events",
            [
                    new BPMNTestCase(1).buildSignaled()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY = builder.buildEventProcess(
            "SignalIntermediateBoundaryEvent", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal end event and a not interrupting signal intermediate boundary event",
            [
                    new BPMNTestCase(1).buildSignaledSubprocess2()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_1 = builder.buildEventProcess(
            "SignalIntermediateBoundaryEventCancel1", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal end event and an interrupting signal intermediate boundary event",
            [
                    new BPMNTestCase(1).buildSignaledBoundary()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_BOUNDARY_CANCEL_2 = builder.buildEventProcess(
            "SignalIntermediateBoundaryEventCancel2", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal end event and an interrupting signal intermediate boundary event on a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).buildSignaledBoundary()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocess", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal intermediate throw event and a not interrupting signal start event",
            [
                    new BPMNTestCase(1).buildSignaledSubprocessNotInterrupted()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_1 = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocessInterrupting1", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal intermediate throw event and an interrupting signal start event",
            [
                    new BPMNTestCase(1).buildSignaledSubprocess1()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE_THROW_SUBPROCESS_INTERRUPTING_2 = builder.buildEventProcess(
            "SignalIntermediateThrowEventSubprocessInterrupting2", "de.uniba.dsg", "1.0", "A test with a subprocess with a signal intermediate throw event and an interrupting signal start event in a sub process with a following normal sequence flow",
            [
                    new BPMNTestCase(1).buildSignaledSubprocess2()
            ]
    )

    public static final BPMNProcess SIGNAL_TWO_STARTS = builder.buildEventProcess(
            "SignalTwoStartEvents", "de.uniba.dsg", "1.0", "A test for signaling two signal start events in two pools",
            [
                    new BPMNTestCase(1).buildTwoSignaled()
            ]
    )

    public static final BPMNProcess CANCEL = builder.buildEventProcess(
            "CancelEvent", "de.uniba.dsg", "1.0", "A simple test for canceling a transaction",
            [
                    new BPMNTestCase(1).buildTransaction()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_END = builder.buildEventProcess(
            "CompensationBoundaryEnd", "de.uniba.dsg", "1.0", "A test for a compensation end and a compensation boundary event",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_THROW = builder.buildEventProcess(
            "CompensationBoundaryThrow", "de.uniba.dsg", "1.0", "A test for a compensation throw and a compensation boundary event",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_TASK_THROW = builder.buildEventProcess(
            "CompensationBoundaryTaskThrow", "de.uniba.dsg", "1.0", "A test for a compensation throw and a compensation boundary event on task level",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final BPMNProcess COMPENSATION_BOUNDARY_TASK_END = builder.buildEventProcess(
            "CompensationBoundaryTaskEnd", "de.uniba.dsg", "1.0", "A test for a compensation end and a compensation boundary event on task level",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final BPMNProcess COMPENSATION_EVENT_SUBPROCESS_END = builder.buildEventProcess(
            "CompensationEventSubprocessEnd", "de.uniba.dsg", "1.0", "A test for a compensation end and an event subprocess with a compensation start event",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final BPMNProcess COMPENSATION_EVENT_SUBPROCESS_THROW = builder.buildEventProcess(
            "CompensationEventSubprocessThrow", "de.uniba.dsg", "1.0", "A test for a compensation throw and an event subprocess with a compensation start event",
            [
                    new BPMNTestCase(1).buildCompensate()
            ]
    )

    public static final List<BPMNProcess> EVENTS = [
            //TIMER_EVENT,
            TIMER_INTERMEDIATE_EVENT,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_1,
            TIMER_INTERMEDIATE_BOUNDARY_EVENT_CANCEL_2,
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
            COMPENSATION_EVENT_SUBPROCESS_THROW
    ].flatten() as List<BPMNProcess>

}

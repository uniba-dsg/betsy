package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNEventProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

//    public static final BPMNProcess TIMER_EVENT = builder.buildEventProcess(
//            "TimerStartEvent", "de.uniba.dsg", "1.0", "Tests for the TimerEvent",
//            [
//                    new BPMNTestCase(number: 1, selfStarting: true, delay: 2000).buildSimple()
//            ]
//    )

    public static final BPMNProcess ERROR_END = builder.buildEventProcess(
            "ErrorEndEvent", "de.uniba.dsg", "1.0", "A simple test for the ErrorEndEvent",
            [
                    new BPMNTestCase(1).buildSimpleError()
            ]
    )

    public static final BPMNProcess ERROR_INTERMEDIATE = builder.buildEventProcess(
            "ErrorIntermediateEvent", "de.uniba.dsg", "1.0", "A simple test for the error intermediate event",
            [
                    new BPMNTestCase(1).buildSubprocess()
            ]
    )

    public static final BPMNProcess ERROR_START = builder.buildEventProcess(
            "ErrorStartEvent", "de.uniba.dsg", "1.0", "A test for the error start event in an event triggered sub process",
            [
                    new BPMNTestCase(1).buildErrorStartEvent()
            ]
    )

    public static final BPMNProcess LINK = builder.buildEventProcess(
            "LinkEvent", "de.uniba.dsg", "1.0", "A simple test for link events",
            [
                    new BPMNTestCase(1).buildSimple()
            ]
    )

    public static final BPMNProcess SIGNAL_INTERMEDIATE = builder.buildEventProcess(
            "SignalIntermediateEvent", "de.uniba.dsg", "1.0", "A simple test for signal intermediate events",
            [
                    new BPMNTestCase(1).buildSignaled()
            ]
    )

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

    public static final BPMNProcess SIGNAL_START_SUBPROCESS = builder.buildEventProcess(
            "SignalStartEventSubprocess", "de.uniba.dsg", "1.0", "A test for the signal start event in an event triggered sub process",
            [
                    new BPMNTestCase(1).buildSignaledSubprocess()
            ]
    )

    public static final BPMNProcess SIGNAL_START_TWO_SUBPROCESSES = builder.buildEventProcess(
            "SignalTwoStartEventSubprocesses", "de.uniba.dsg", "1.0", "A test for signaling two signal start events in event triggered sub processes",
            [
                    new BPMNTestCase(1).buildTwoSignaledSubprocesses()
            ]
    )

    public static final List<BPMNProcess> EVENTS = [
//            TIMER_EVENT,
            ERROR_END,
            ERROR_INTERMEDIATE,
            ERROR_START,
            LINK,
            SIGNAL_INTERMEDIATE,
            SIGNAL_START_END,
            SIGNAL_INTERMEDIATE_START,
            SIGNAL_START_SUBPROCESS,
            SIGNAL_START_TWO_SUBPROCESSES
    ].flatten() as List<BPMNProcess>

}

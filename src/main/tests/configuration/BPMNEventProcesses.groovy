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

    public static final List<BPMNProcess> EVENTS = [
//            TIMER_EVENT,
            ERROR_END,
            ERROR_INTERMEDIATE,
            LINK,
            SIGNAL_INTERMEDIATE
    ].flatten() as List<BPMNProcess>

}

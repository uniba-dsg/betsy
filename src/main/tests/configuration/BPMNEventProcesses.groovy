package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNEventProcesses {

    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess TIMER_EVENT = builder.buildEventProcess(
            "TimerEvent", "de.uniba.dsg", "1.0", "Tests for the TimerEvent",
            [
                    new BPMNTestCase(1).buildSimple()
            ]
    )

    public static final BPMNProcess ERROR_END = builder.buildEventProcess(
            "ErrorEndEvent", "de.uniba.dsg", "1.0", "A simple Test for the ErrorEndEvent",
            [
                    new BPMNTestCase(1).buildSimpleError()
            ]
    )

    public static final List<BPMNProcess> EVENTS = [
            TIMER_EVENT,
            ERROR_END
    ].flatten() as List<BPMNProcess>

}

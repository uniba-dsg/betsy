package configuration

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

class BPMNTaskProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SIMPLE = builder.buildTaskProcess(
            "SequenceFlow", "de.uniba.dsg", "1.0", "A Test for the basic process using a script task",
            [
                    new BPMNTestCase().buildSimple()
            ]
    )

    public static final BPMNProcess MULTI_SEQUENTIAL = builder.buildTaskProcess(
            "MultiSequencialTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times sequentially instantiated script task",
            [
                    new BPMNTestCase().buildMulti3()
            ]
    )

    public static final BPMNProcess MULTI_PARALLEL = builder.buildTaskProcess(
            "MultiParallelTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times parallel instantiated script task",
            [
                    new BPMNTestCase().buildMulti3()
            ]
    )

    public static final BPMNProcess LOOP = builder.buildTaskProcess(
            "LoopTask", "de.uniba.dsg", "1.0", "A simple Test for a 3 times looped script task",
            [
                    new BPMNTestCase().buildMulti3()
            ]
    )

    public static final List<BPMNProcess> TASKS = [
            SIMPLE,
            MULTI_SEQUENTIAL,
            MULTI_PARALLEL,
            LOOP
    ].flatten() as List<BPMNProcess>
}

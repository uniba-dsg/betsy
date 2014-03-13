package configuration

import betsy.data.BPMNProcess

class BPMNTaskProcesses {
    static BPMNProcessBuilder builder = new BPMNProcessBuilder()

    public static final BPMNProcess SIMPLE = builder.buildTaskProcess(
            "simple", "SimpleApplication", "org.camunda.bpm.dsg", "1.0", "A Test for the basic process using a script task"
    )

    public static final List<BPMNProcess> TASKS = [
            SIMPLE
    ].flatten() as List<BPMNProcess>
}

package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

/**
 * This class bundles processes that contain data objects which are implemented differently for each engine.
 */
class DataProcesses {

    public static final BPMNProcess DATA_READ_WRITE_STRING = BPMNProcessBuilder.buildDataProcess(
        "Data_ReadWrite_String", "A process consisting of three scriptTasks, " +
                    "the second of which writes a string data object, and the third of which reads the string " +
                    "data object and writes an assertion token into the final log if successful.",
        new BPMNTestCase().assertDataCorrect()
    );

    public static final List<BPMNProcess> DATA = Arrays.asList(
        DATA_READ_WRITE_STRING
    );

}

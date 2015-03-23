package configuration.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;

import java.util.Arrays;
import java.util.List;

/**
 * This class bundles processes that contain data objects which are implemented differently for each engine.
 */
class DataProcesses {

    public static final BPMNProcess DATA_READ_ONLY_STRING = BPMNProcessBuilder.buildDataProcess(
            "Data_ReadOnly_String", "A process containing a scriptTask which writes " +
                    "a predefined string variable into a log file.",
            new BPMNTestCase().assertDataCorrect()
    );

    public static final List<BPMNProcess> DATA = Arrays.asList(
            DATA_READ_ONLY_STRING

    );

}

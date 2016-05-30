package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;

import java.util.Arrays;
import java.util.List;

/**
 * This class bundles processes that contain data objects which are implemented differently for each engine.
 */
class DataProcesses {

    private static final Construct DATA_OBJECT = new Construct(Groups.DATA, "DataObject", "DataObjects are item-aware "
            + "elements visually displayed on a Process diagram. (see BPMN spec pp.204-205)");
    private static final Construct PROPERTY = new Construct(Groups.DATA, "Property", "Properties are item-aware elements "
            + "not visually displayed on a Process diagram. (see BPMN spec pp.208-209)");

    public static final EngineIndependentProcess DATA_OBJECT_READ_WRITE_STRING = BPMNProcessBuilder.buildDataProcess(
            "A process consisting of three scriptTasks, " +
                    "the second of which writes a string data object, and the third of which reads the string " +
                    "data object and writes an assertion token into the final log if successful.",
            new Feature(DATA_OBJECT, "DataObject_ReadWrite_String"),
            new BPMNTestCase().assertDataCorrect()
    );
    public static final EngineIndependentProcess PROPERTY_READ_WRITE_STRING = BPMNProcessBuilder.buildDataProcess(
            "A process consisting of three scriptTasks, " +
                    "the second of which writes a string property, and the third of which reads the string " +
                    "property and writes an assertion token into the final log if successful.",
            new Feature(PROPERTY, "Property_ReadWrite_String"),
            new BPMNTestCase().assertDataCorrect()
    );

    public static final List<EngineIndependentProcess> DATA = Arrays.asList(
            DATA_OBJECT_READ_WRITE_STRING,
            PROPERTY_READ_WRITE_STRING
    );

}

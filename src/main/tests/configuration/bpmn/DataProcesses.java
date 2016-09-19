package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCaseBuilder;
import pebl.feature.FeatureSet;
import pebl.test.Test;
import pebl.feature.Feature;

import java.util.Arrays;
import java.util.List;

/**
 * This class bundles processes that contain data objects which are implemented differently for each engine.
 */
class DataProcesses {

    private static final FeatureSet DATA_OBJECT = new FeatureSet(Groups.DATA, "DataObject", "DataObjects are item-aware "
            + "elements visually displayed on a Process diagram. (see BPMN spec pp.204-205)");
    private static final FeatureSet PROPERTY = new FeatureSet(Groups.DATA, "Property", "Properties are item-aware elements "
            + "not visually displayed on a Process diagram. (see BPMN spec pp.208-209)");

    public static final Test DATA_OBJECT_READ_WRITE_STRING = BPMNProcessBuilder.buildDataProcess(
            "A process consisting of three scriptTasks, " +
                    "the second of which writes a string data object, and the third of which reads the string " +
                    "data object and writes an assertion token into the final log if successful.",
            new Feature(DATA_OBJECT, "DataObject_ReadWrite_String"),
            new BPMNTestCaseBuilder().assertDataCorrect()
    );
    public static final Test PROPERTY_READ_WRITE_STRING = BPMNProcessBuilder.buildDataProcess(
            "A process consisting of three scriptTasks, " +
                    "the second of which writes a string property, and the third of which reads the string " +
                    "property and writes an assertion token into the final log if successful.",
            new Feature(PROPERTY, "Property_ReadWrite_String"),
            new BPMNTestCaseBuilder().assertDataCorrect()
    );

    public static final List<Test> DATA = Arrays.asList(
            DATA_OBJECT_READ_WRITE_STRING,
            PROPERTY_READ_WRITE_STRING
    );

}

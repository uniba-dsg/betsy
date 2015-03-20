package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stlmaass on 19.03.2015.
 */
public class BPMNEnginesUtil {

    public static void substituteSpecificErrorsForGenericError(BPMNTestCase testCase, Path logDir) {
        if(testCase.getAssertions().contains(BPMNAssertions.ERROR_GENERIC.toString())) {
            List<String> toReplace = new ArrayList<>();
            toReplace.add(BPMNAssertions.ERROR_DEPLOYMENT.toString());
            toReplace.add(BPMNAssertions.ERROR_RUNTIME.toString());
            FileTasks.replaceLogFileContent(toReplace, BPMNAssertions.ERROR_GENERIC.toString(), logDir);
        }
    }


}

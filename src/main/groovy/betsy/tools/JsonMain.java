package betsy.tools;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonMain {

    public static void main(String[] args) {
        Path workingDirectory = Paths.get(".");
        JsonGeneratorEngines.generateEnginesJson(workingDirectory);
        JsonGeneratorFeatureTree.generatesConstructsJsonWithReallyAllProcesses(workingDirectory);
        JsonGeneratorTestsEngineIndependent.generateTestsEngineIndependentJsonWithReallyAllProcesses(workingDirectory);
    }

    public static void writeIntoSpecificFolder(Path folder) {
        JsonGeneratorEngines.generateEnginesJson(folder);
        JsonGeneratorFeatureTree.generatesConstructsJson(folder);
        JsonGeneratorTestsEngineIndependent.generateTestsEngineIndependentJson(folder);
    }

}

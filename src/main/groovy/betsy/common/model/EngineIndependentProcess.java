package betsy.common.model;

import betsy.common.HasName;
import betsy.common.model.feature.Feature;
import betsy.common.model.feature.FeatureDimension;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EngineIndependentProcess implements Comparable<EngineIndependentProcess>, HasName, FeatureDimension {

    private final Path process;
    private final List<TestCase> testCases;
    private final Feature feature;
    private final List<Path> files = new ArrayList<>();
    private final String description;

    public EngineIndependentProcess(Path process, String description, List<? extends TestCase> testCases, Feature feature) {
        this.process = process;
        this.feature = feature;
        this.description = description;
        this.testCases = Collections.unmodifiableList(uniqueifyTestCaseNames(new ArrayList<>(testCases)));
    }

    public List<String> getFileNames() {
        return files.stream().map((p) -> p.getFileName().toString()).collect(Collectors.toList());
    }

    private List<TestCase> uniqueifyTestCaseNames(List<TestCase> testCases) {
        // make sure that the tests have different numbers
        for (int counter = 1; counter <= testCases.size(); counter++) {
            TestCase testCase = testCases.get(counter - 1);
            testCase.setNumber(counter);
        }

        // only when not already unique
        if (!hasUniqueTestCaseNames(testCases)) {
            return testCases;
        }

        // make test case names unique
        for (TestCase testCase : testCases) {
            testCase.setName(testCase.getName() + "-" + testCase.getNumber());
        }

        return testCases;
    }

    private boolean hasUniqueTestCaseNames(List<TestCase> testCases) {
        return testCases.stream().map(TestCase::getName).distinct().count() == testCases.size();
    }

    /**
     * Returns the process language used by the given process.
     *
     * @return the process language used by the given process.
     */
    public ProcessLanguage getProcessLanguage() {
        return ProcessLanguage.getByPath(getProcess());
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!getClass().equals(o.getClass())) {
            return false;
        }

        EngineIndependentProcess that = (EngineIndependentProcess) o;

        return process.toString().equals(that.process.toString());

    }

    String getProcessFileNameWithoutExtension() {
        return FileTasks.getFilenameWithoutExtension(getProcess());
    }

    public int hashCode() {
        return process.hashCode();
    }

    @Override
    public int compareTo(EngineIndependentProcess o) {
        return process.compareTo(o.process);
    }

    public Path getProcess() {
        return this.process;
    }

    public String getDescription() {
        return getFeature().description;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    private void setTestCases(List<TestCase> testCases) {
        uniqueifyTestCaseNames(testCases);
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    @Override
    public String getName() {
        return getFeature().getName();
    }

    @Override
    public Feature getFeature() {
        return feature;
    }
}

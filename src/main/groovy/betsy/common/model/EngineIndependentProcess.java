package betsy.common.model;

import betsy.common.HasName;
import betsy.common.model.feature.Feature;
import betsy.common.model.feature.FeatureDimension;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EngineIndependentProcess implements Comparable<EngineIndependentProcess>, HasName, FeatureDimension {

    private final Path process;
    private final List<TestCase> testCases;
    private final Feature feature;
    private final List<Path> files;
    private final String description;

    public EngineIndependentProcess(Path process, String description, List<? extends TestCase> testCases, Feature feature) {
        this(process, description, testCases, feature, Collections.emptyList());
    }

    public EngineIndependentProcess(Path process, String description, List<? extends TestCase> testCases, Feature feature, List<Path> files) {
        this.process = Objects.requireNonNull(process);
        this.feature = Objects.requireNonNull(feature);
        this.description = Objects.requireNonNull(description);
        this.testCases = Collections.unmodifiableList(uniqueifyTestCaseNames(new ArrayList<>(Objects.requireNonNull(testCases))));
        this.files = Objects.requireNonNull(files);
    }

    public EngineIndependentProcess withNewProcess(Path process) {
        return new EngineIndependentProcess(process, description, testCases, feature, files);
    }

    public EngineIndependentProcess withNewTestCases(List<TestCase> testCases) {
        return new EngineIndependentProcess(process, description, testCases, feature, files);
    }

    public List<Path> getFiles() {
        return files;
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
        return description;
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

    public List<Path> getFiles(Predicate<Path> predicate) {
        return getFiles().stream().filter(predicate).collect(Collectors.toList());
    }

}


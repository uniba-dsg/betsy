package betsy.common.model;

import betsy.common.HasName;
import betsy.common.HasPath;
import betsy.common.engines.ProcessLanguage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractProcess<TC extends TestCase, E extends HasPath> implements Comparable, HasName, ProcessFolderStructure {

    public void setTestCases(List<TC> testCases) {
        uniqueifyTestCaseNames(testCases);
        this.testCases = testCases;
    }

    private void uniqueifyTestCaseNames(List<TC> testCases) {
        // group by name of test case
        for (int counter = 1; counter <= testCases.size(); counter++) {
            TestCase testCase = testCases.get(counter - 1);
            testCase.setName(testCase.getName() + "-" + counter);
            testCase.setNumber(counter);
        }
    }

    public abstract AbstractProcess<TC,E> createCopyWithoutEngine();

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
        return getNormalizedId();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!getClass().equals(o.getClass())) {
            return false;
        }

        AbstractProcess that = (AbstractProcess) o;

        return process.toString().equals(that.process.toString());

    }

    public int hashCode() {
        return process.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return process.compareTo(((AbstractProcess) o).process);
    }

    private Path process;
    private String description = "";
    private List<TC> testCases = new ArrayList<>();
    private E engine;

    public Path getProcess() { return this.process; }

    public void setProcess(Path process) {
        this.process = process;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TC> getTestCases() {
        return testCases;
    }

    public E getEngine() {
        return engine;
    }

    public void setEngine(E engine) {
        this.engine = engine;
    }
}
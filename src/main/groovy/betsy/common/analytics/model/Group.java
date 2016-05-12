package betsy.common.analytics.model;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Group implements Comparable<Group> {

    private final String name;
    private SortedSet<Test> tests = new TreeSet<>();

    public Group(String name) {
        this.name = name;
    }

    public List<Result> getResultsPerEngine(Engine engine) {
        return tests.stream().map( (test) -> test.getEngineToResult().get(engine)).collect(Collectors.toList());
    }

    @Override
    public int compareTo(Group o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Group{" + "name='" + name + "\'" + "}";
    }

    public SortedSet<Test> getTests() {
        return tests;
    }

    public void setTests(SortedSet<Test> tests) {
        this.tests = tests;
    }

    public String getName() {
        return name;
    }
}

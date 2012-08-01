package betsy.executables.reporting.csv


class Group implements Comparable<Group> {
    String name

    SortedSet<Test> tests = new TreeSet<Test>()

    @Override
    int compareTo(Group o) {
        return name.compareTo(o.name)
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                '}';
    }
}

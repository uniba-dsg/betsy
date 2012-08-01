package betsy.executables.reporting.csv


class Test implements Comparable<Test> {
    String name
    SortedMap<Engine, Result> engineToResult = new TreeMap<Engine, Result>()

    String getFullName() {
        name
    }

    Result.Support getSupport() {
        if (engineToResult.values().every { it.partial == Result.Support.NONE}) {
            Result.Support.NONE
        } else if (engineToResult.values().every { it.partial == Result.Support.TOTAL}) {
            Result.Support.TOTAL
        } else {
            Result.Support.PARTIAL
        }
    }

    @Override
    int compareTo(Test o) {
        return fullName.compareTo(o.fullName)
    }

    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                ", engineToResult=" + engineToResult +
                '}';
    }
}

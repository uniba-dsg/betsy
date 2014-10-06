package betsy.common.analytics.model


class Engine implements Comparable<Engine> {
    String name

    @Override
    int compareTo(Engine o) {
        return name.compareTo(o.name)
    }

    @Override
    public String toString() {
        return "Engine{" +
                "name='" + name + '\'' +
                '}';
    }
}

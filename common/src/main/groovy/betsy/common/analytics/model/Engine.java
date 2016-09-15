package betsy.common.analytics.model;

public class Engine implements Comparable<Engine> {

    private final String name;

    public Engine(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Engine o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "EngineExtended{" + "name='" + name + "\'" + "}";
    }

    public String getName() {
        return name;
    }

}

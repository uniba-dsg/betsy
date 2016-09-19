package pebl.tool;

import java.util.Objects;

import pebl.HasID;
import pebl.HasName;

public class Tool implements HasID, HasName {

    public final String name;
    public final String version;

    public Tool(String name, String version) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String getID() {
        return String.join(SEPARATOR, name, version);
    }
}

package betsy.common.model;

import java.util.Objects;

import betsy.common.HasName;
import betsy.common.util.GitUtil;

public class Tool implements HasID, HasName {

    public static final Tool BETSY = new Tool("betsy", GitUtil.getGitCommit());

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

    @Override
    public String getID() {
        return String.join(SEPARATOR, name, version);
    }
}

package betsy.common.model;

import betsy.common.HasName;

public interface IsEngine extends HasName {

    Engine getEngineId();

    default String getName() {
        return getEngineId().toString();
    }

}

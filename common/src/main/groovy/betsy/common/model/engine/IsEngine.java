package betsy.common.model.engine;

import betsy.common.HasName;

public interface IsEngine extends HasName, EngineDimension {

    default String getName() {
        return getEngineID();
    }

}

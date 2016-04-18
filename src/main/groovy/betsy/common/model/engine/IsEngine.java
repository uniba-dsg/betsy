package betsy.common.model.engine;

import betsy.common.HasName;
import betsy.common.model.engine.Engine;

public interface IsEngine extends HasName, EngineDimension {

    default String getName() {
        return getEngineID();
    }

}

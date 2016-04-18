package betsy.common.model.engine;

import betsy.common.model.HasID;

public interface EngineDimension {

    Engine getEngineObject();

    default String getEngineID() {
        return getEngineObject().getID();
    }

}

package betsy.common.model.engine;

public interface EngineDimension {

    EngineExtended getEngineObject();

    default String getEngineID() {
        return getEngineObject().getID();
    }

}

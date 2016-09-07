package betsy.common.model.engine;

public interface EngineDimension {

    Engine getEngineObject();

    default String getEngineID() {
        return getEngineObject().getID();
    }

}

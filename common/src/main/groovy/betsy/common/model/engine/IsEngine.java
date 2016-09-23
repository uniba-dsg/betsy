package betsy.common.model.engine;

public interface IsEngine extends EngineDimension, pebl.HasName {

    default String getName() {
        return getEngineID();
    }

}

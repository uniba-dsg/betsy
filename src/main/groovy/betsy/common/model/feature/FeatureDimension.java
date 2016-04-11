package betsy.common.model.feature;

import betsy.common.model.ProcessLanguage;

public interface FeatureDimension {

    default ProcessLanguage getLanguage() {
        return getGroup().processLanguage;
    }

    default Group getGroup() {
        return getConstruct().group;
    }

    default Construct getConstruct() {
        return getFeature().construct;
    }

    Feature getFeature();

}

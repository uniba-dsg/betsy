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

    /**
     * Is globally unique
     *
     * @return LANGUAGE__FEATURE
     */
    default String getLanguageFeatureID() {
        return String.join("__", getLanguage().name(), getFeature().getName());
    }

    /**
     * Is not globally unique, only for each language
     *
     * @return GROUP__FEATURE
     */
    default String getGroupFeatureID() {
        return String.join("__", getGroup().getName(), getFeature().getName());
    }

    Feature getFeature();

}

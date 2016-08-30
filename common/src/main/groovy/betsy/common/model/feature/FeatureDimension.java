package betsy.common.model.feature;

public interface FeatureDimension {

    default Capability getCapability() {
        return getLanguage().capability;
    }

    default Language getLanguage() {
        return getGroup().language;
    }

    default Group getGroup() {
        return getFeatureSet().group;
    }

    default FeatureSet getFeatureSet() {
        return getFeature().featureSet;
    }

    /**
     * Is globally unique
     *
     * @return LANGUAGE__FEATURE
     */
    default String getLanguageFeatureID() {
        return String.join("__", getLanguage().getName(), getFeature().getName());
    }

    /**
     * Is not globally unique, only for each language
     *
     * @return GROUP__FEATURE
     */
    default String getGroupFeatureID() {
        return String.join("__", getGroup().getName(), getFeature().getName());
    }

    default String getFeatureID() {
        return getFeature().getID();
    }

    Feature getFeature();

}

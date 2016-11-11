package pebl.benchmark.feature;

public interface FeatureDimension {

    default Capability getCapability() {
        return getLanguage().getCapability();
    }

    default Language getLanguage() {
        return getGroup().getLanguage();
    }

    default Group getGroup() {
        return getFeatureSet().getGroup();
    }

    default FeatureSet getFeatureSet() {
        return getFeature().getFeatureSet();
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
        return getFeature().getId();
    }

    Feature getFeature();

}

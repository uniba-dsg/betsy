package pebl.xsd;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.feature.Capability;
import pebl.feature.Feature;
import pebl.feature.FeatureSet;
import pebl.feature.Group;
import pebl.feature.Language;

@XmlRootElement
public class Features {

    @XmlElement(name="feature")
    public List<Feature> features = new LinkedList<>();
    @XmlElement(name="featureSet")
    public List<FeatureSet> featureSets = new LinkedList<>();
    @XmlElement(name="group")
    public List<Group> groups = new LinkedList<>();
    @XmlElement(name="language")
    public List<Language> languages = new LinkedList<>();
    @XmlElement(name="capability")
    public List<Capability> capabilities = new LinkedList<>();

    public Features() {

    }

    public Features(List<Feature> features) {
        this.features.addAll(features);
        this.featureSets.addAll(features.stream().map(Feature::getFeatureSet).distinct().collect(Collectors.toList()));
        this.groups.addAll(featureSets.stream().map(FeatureSet::getGroup).distinct().collect(Collectors.toList()));
        this.languages.addAll(groups.stream().map(Group::getLanguage).distinct().collect(Collectors.toList()));
        this.capabilities.addAll(languages.stream().map(Language::getCapability).distinct().collect(Collectors.toList()));
    }
}

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

    @XmlElement(name="capability")
    public List<Capability> capabilities = new LinkedList<>();

    public Features() {

    }

    public Features(List<Feature> features) {
        this.capabilities.addAll(features.stream()
                .map(f -> f.getFeatureSet())
                .map(f -> f.getGroup())
                .map(f -> f.getLanguage())
                .map(f -> f.getCapability())
                .distinct()
                .collect(Collectors.toList()));
    }
}

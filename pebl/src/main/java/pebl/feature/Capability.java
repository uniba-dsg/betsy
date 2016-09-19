package pebl.feature;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import pebl.HasName;
import pebl.HasID;

@XmlAccessorType(XmlAccessType.NONE)
public class Capability implements HasID, HasName {

    private final String name;
    private final ResultFormat resultFormat;
    private final List<Language> languages = new LinkedList<>();

    Capability() {
        this("", new ResultFormat());
    }

    public Capability(String name, ResultFormat resultFormat) {
        this.name = Objects.requireNonNull(name);
        this.resultFormat = Objects.requireNonNull(resultFormat);
    }

    @Override
    @XmlID
    @XmlElement(required = true)
    public String getID() {
        return name;
    }

    @Override
    @XmlElement(required = true)
    public String getName() {
        return name;
    }

    @XmlElement(required = true)
    public ResultFormat getResultFormat() {
        return resultFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Capability construct = (Capability) o;
        return Objects.equals(getID(), construct.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID());
    }

    void addLanguage(Language language) {
        this.languages.add(language);
    }

    @XmlIDREF
    public List<Language> getLanguages() {
        return Collections.unmodifiableList(languages);
    }
}

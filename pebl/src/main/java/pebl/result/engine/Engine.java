package pebl.result.engine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import pebl.ProcessLanguage;
import pebl.HasID;
import pebl.HasName;

import static java.util.Objects.requireNonNull;

@XmlAccessorType(XmlAccessType.NONE)
public final class Engine implements HasID, HasName {

    public static final String DELIMITER = "--";

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String version;

    @XmlElement(required = true)
    private final List<String> configuration;

    @XmlElement(required = true)
    private final ProcessLanguage language;

    public Engine() {
        this(ProcessLanguage.UNKNOWN, "", "");
    }

    public Engine(ProcessLanguage language, String name, String version) {
        this(language, name, version, Collections.emptyList());
    }

    public Engine(ProcessLanguage language, String name, String version, String configuration) {
        this(language, name, version, Collections.singletonList(configuration));
    }

    public Engine(ProcessLanguage language, String name, String version, List<String> configuration) {
        this.language = language;
        this.name = requireNonNull(name);
        this.version = requireNonNull(version);

        List<String> values = new LinkedList<>();
        values.addAll(configuration);
        this.configuration = requireNonNull(values);
    }

    public String toString() {
        return getNormalizedId();
    }

    private String getNormalizedId() {
        return getId().replaceAll(DELIMITER, "__").replaceAll("\\.", "_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Engine engine = (Engine) o;
        return Objects.equals(toString(), engine.toString());
    }

    public ProcessLanguage getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    private String getId() {
        List<String> values = new LinkedList<>();
        values.add(name);
        values.add(version);
        values.addAll(configuration);

        return String.join(DELIMITER, values);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<String> getConfiguration() {
        return Collections.unmodifiableList(configuration);
    }

    @Override
    @XmlAttribute(required = true)
    @XmlID
    public String getID() {
        return getNormalizedId();
    }

}

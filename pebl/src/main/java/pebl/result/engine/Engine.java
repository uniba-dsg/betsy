package pebl.result.engine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import pebl.HasExtension;
import pebl.HasId;
import pebl.HasName;

import static java.util.Objects.requireNonNull;

@XmlAccessorType(XmlAccessType.NONE)
public final class Engine implements HasId, HasName, HasExtension {

    public static final String DELIMITER = "--";

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String version;

    @XmlElement(required = true)
    private final List<String> configuration;

    @XmlElement(required = true)
    private final String language;

    @XmlElement
    private final Map<String, String> extension;

    public Engine() {
        this("", "", "");
    }

    public Engine(String language, String name, String version) {
        this(language, name, version, Collections.emptyList());
    }

    public Engine(String language, String name, String version, String configuration) {
        this(language, name, version, Collections.singletonList(configuration));
    }

    public Engine(String language, String name, String version, List<String> configuration) {
        this.language = language;
        this.name = requireNonNull(name);
        this.version = requireNonNull(version);

        this.extension = Collections.emptyMap();

        List<String> values = new LinkedList<>();
        values.addAll(configuration);
        this.configuration = requireNonNull(values);
    }

    public String toString() {
        return getNormalizedId();
    }

    private String getNormalizedId() {
        return getIdentifier().replaceAll(DELIMITER, "__").replaceAll("\\.", "_");
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

    public String getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(toString());
    }

    private String getIdentifier() {
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
    public String getId() {
        return getNormalizedId();
    }

    @Override public Map<String, String> getExtension() {
        return extension;
    }

    @Override
    public Engine addExtension(String key, String value) {
        extension.put(key, value);

        return this;
    }
}

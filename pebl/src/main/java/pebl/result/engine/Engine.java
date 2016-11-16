package pebl.result.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pebl.HasExtensions;
import pebl.HasId;
import pebl.HasName;
import pebl.MapAdapter;

import static java.util.Objects.requireNonNull;

@XmlAccessorType(XmlAccessType.NONE)
public final class Engine implements HasId, HasName, HasExtensions {

    public static final String DELIMITER = "--";

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String version;

    @XmlElement(required = true)
    @XmlList
    private final List<String> configuration;

    @XmlElement(required = true)
    private final String language;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions;

    public Engine() {
        this("", "", "");
    }

    public Engine(String language, String name, String version) {
        this(language, name, version, Collections.emptyList());
    }

    public Engine(String language, String name, String version, List<String> configuration) {
        this.language = language;
        this.name = requireNonNull(name);
        this.version = requireNonNull(version);

        this.extensions = new HashMap<>();

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

    @Override public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public Engine addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }
}

package pebl.result.tool;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import pebl.HasExtensions;
import pebl.HasId;
import pebl.HasName;
import pebl.MapAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class Tool implements HasId, HasName, HasExtensions {

    @XmlElement(required = true)
    private final String name;

    @XmlElement(required = true)
    private final String version;

    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String, String> extensions;

    public Tool() {
        this("", "");
    }

    public Tool(String name, String version) {
        this.name = Objects.requireNonNull(name);
        this.version = Objects.requireNonNull(version);
        this.extensions = Collections.emptyMap();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    @XmlAttribute(required = true)
    @XmlID
    public String getId() {
        return String.join(SEPARATOR, name, version);
    }

    @Override
    public Map<String, String> getExtensions() {
        return extensions;
    }

    @Override
    public Tool addExtension(String key, String value) {
        extensions.put(key, value);

        return this;
    }
}

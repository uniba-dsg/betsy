package pebl.result;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class NamedMetrics {

    @XmlElement
    private final Map<String, Metric> values;

    NamedMetrics() {
        this(Collections.emptyMap());
    }

    public NamedMetrics(Map<String, Metric> values) {
        this.values = Objects.requireNonNull(values);
    }

    public Map<String, Metric> getValues() {
        return values;
    }
}

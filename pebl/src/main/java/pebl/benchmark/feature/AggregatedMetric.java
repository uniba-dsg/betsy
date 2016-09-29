package pebl.benchmark.feature;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
public class AggregatedMetric extends Metric {

    public static final String SCRIPT_MAX = "MAX";
    public static final String SCRIPT_COUNT_TRUE = "COUNT_TRUE";
    public static final String SCRIPT_SUM = "SUM";

    @XmlElement(required = true)
    @XmlCDATA
    private final String groovyScript;

    public AggregatedMetric() {
        super();
        groovyScript = "";
    }

    public AggregatedMetric(ValueType type, String name, String description, String unit, String idPrefix,
            String groovyScript) {
        super(type, name, description, unit, idPrefix);
        this.groovyScript = groovyScript;
    }

    public String getGroovyScript() {
        return groovyScript;
    }
}

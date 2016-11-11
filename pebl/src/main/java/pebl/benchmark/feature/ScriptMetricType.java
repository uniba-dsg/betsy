package pebl.benchmark.feature;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
public class ScriptMetricType extends MetricType {

    public static final String SCRIPT_MAX = "MAX";
    public static final String SCRIPT_COUNT_TRUE = "COUNT_TRUE";
    public static final String SCRIPT_SUM = "SUM";

    @XmlValue
    @XmlCDATA
    private final String groovyScript;

    public ScriptMetricType() {
        super();
        groovyScript = "";
    }

    public ScriptMetricType(ValueType type, String name, String description, String unit, String groovyScript) {
        super(type, name, description, unit);
        this.groovyScript = groovyScript;
    }

    public String getGroovyScript() {
        return groovyScript;
    }

    @Override public String toString() {
        final StringBuffer sb = new StringBuffer("ScriptMetricType{");
        sb.append("groovyScript='").append(groovyScript).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

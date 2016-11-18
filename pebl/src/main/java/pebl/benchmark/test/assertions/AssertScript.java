package pebl.benchmark.test.assertions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import pebl.benchmark.test.TestAssertion;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class AssertScript extends TestAssertion {

    @XmlElement
    @XmlCDATA
    private final String groovyScript;

    AssertScript() {
        this("");
    }

    public AssertScript(String groovyScript) {
        this.groovyScript = groovyScript;
    }

    public String getGroovyScript() {
        return groovyScript;
    }

    @Override
    public String toString() {
        return "AssertScript{" +
                "faultString='" + groovyScript + '\'' +
                '}';
    }
}

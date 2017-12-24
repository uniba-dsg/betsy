package pebl.benchmark.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class ScriptBasedWSDLTestPartner extends WSDLTestPartner {

    // operation -> input -> action
    @XmlElement(required = true)
    private final String groovyScript;

    ScriptBasedWSDLTestPartner() {
        this(Paths.get(""), "", "");
    }

    public ScriptBasedWSDLTestPartner(Path interfaceDescription, String publishedUrl, String groovyScript) {
        super(publishedUrl, interfaceDescription);
        this.groovyScript = groovyScript;
    }

    public String getGroovyScript() {
        return groovyScript;
    }
}

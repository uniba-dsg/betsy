package pebl.test.partner.rules;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class IntegerOutputBasedOnScriptResult extends Output {

    @XmlElement(required = true)
    private final String script;

    IntegerOutputBasedOnScriptResult() {
        this("");
    }

    public IntegerOutputBasedOnScriptResult(String script) {
        this.script = Objects.requireNonNull(script);
    }

    public String getScript() {
        return script;
    }
}

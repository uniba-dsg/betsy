package pebl.test.partner.rules;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class IntegerOutputBasedOnScriptResult extends Output {

    private final String script;

    IntegerOutputBasedOnScriptResult() {
        this("");
    }

    public IntegerOutputBasedOnScriptResult(String script) {
        this.script = Objects.requireNonNull(script);
    }

    @XmlElement(required = true)
    public String getScript() {
        return script;
    }
}

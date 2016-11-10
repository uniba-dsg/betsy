package pebl.benchmark.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.partner.rules.OperationInputOutputRule;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class RuleBasedWSDLTestPartner extends WSDLTestPartner {

    // operation -> input -> action
    @XmlElement(name="rule")
    @XmlElementWrapper( name="rules" )
    private final List<OperationInputOutputRule> rules;

    RuleBasedWSDLTestPartner() {
        this(Paths.get(""), "");
    }

    public RuleBasedWSDLTestPartner(Path interfaceDescription, String publishedUrl, OperationInputOutputRule... operationInputOutputRules) {
        super(publishedUrl, interfaceDescription);
        this.rules = new LinkedList<>(Arrays.asList(operationInputOutputRules));
    }

    public List<OperationInputOutputRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

}

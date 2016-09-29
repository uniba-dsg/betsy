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

import pebl.benchmark.test.partner.rules.OperationInputOutputRule;

@XmlAccessorType(XmlAccessType.NONE)
public class InternalWSDLTestPartner extends WSDLTestPartner {

    @Override
    public String getPublishedURL() {
        return this.publishedUrl;
    }

    @XmlElement(required = true)
    private final Path interfaceDescription;

    @XmlElement(required = true)
    private final String publishedUrl;

    // operation -> input -> action
    @XmlElement
    private final List<OperationInputOutputRule> rules;

    InternalWSDLTestPartner() {
        this(Paths.get(""), "");
    }

    public InternalWSDLTestPartner(Path interfaceDescription, String publishedUrl, OperationInputOutputRule... operationInputOutputRules) {
        this.publishedUrl = publishedUrl;
        this.interfaceDescription = Objects.requireNonNull(interfaceDescription);
        this.rules = new LinkedList<>(Arrays.asList(operationInputOutputRules));
    }

    public List<OperationInputOutputRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public Path getInterfaceDescription() {
        return interfaceDescription;
    }

    public String getPublishedUrl() {
        return publishedUrl;
    }

}

package pebl.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

import pebl.test.partner.rules.OperationInputOutputRule;

public class InternalWSDLTestPartner extends WSDLTestPartner {

    @Override
    public String getPublishedURL() {
        return this.publishedUrl;
    }

    private final Path interfaceDescription;

    private final String publishedUrl;

    // operation -> input -> action
    private final List<OperationInputOutputRule> rules;

    InternalWSDLTestPartner() {
        this(Paths.get(""), "");
    }

    public InternalWSDLTestPartner(Path interfaceDescription, String publishedUrl, OperationInputOutputRule... operationInputOutputRules) {
        this.publishedUrl = publishedUrl;
        this.interfaceDescription = Objects.requireNonNull(interfaceDescription);
        this.rules = Collections.unmodifiableList(new LinkedList<>(Arrays.asList(operationInputOutputRules)));
    }

    @XmlElement
    public List<OperationInputOutputRule> getRules() {
        return rules;
    }

    @XmlElement(required = true)
    public Path getInterfaceDescription() {
        return interfaceDescription;
    }

    @XmlElement(required = true)
    public String getPublishedUrl() {
        return publishedUrl;
    }

}

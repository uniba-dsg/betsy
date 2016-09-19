package pebl.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.XmlElement;

public class ExternalWSDLTestPartner extends WSDLTestPartner {

    private final String url;
    private final Path wsdl;

    ExternalWSDLTestPartner() {
        this("", Paths.get(""));
    }

    public ExternalWSDLTestPartner(String url, Path wsdl) {
        this.url = url;
        this.wsdl = wsdl;
    }

    @Override
    public String getPublishedURL() {
        return url;
    }

    @XmlElement(required = true)
    public String getUrl() {
        return url;
    }

    @XmlElement(required = true)
    public Path getWsdl() {
        return wsdl;
    }
}

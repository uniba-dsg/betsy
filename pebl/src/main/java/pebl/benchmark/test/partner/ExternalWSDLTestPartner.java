package pebl.benchmark.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class ExternalWSDLTestPartner extends WSDLTestPartner {

    @XmlElement(required = true)
    private final String url;

    @XmlElement(required = true)
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

    public String getUrl() {
        return url;
    }

    public Path getWsdl() {
        return wsdl;
    }
}

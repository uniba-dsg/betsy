package pebl.benchmark.test.partner;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pebl.benchmark.test.TestPartner;

@XmlRootElement
public class WSDLTestPartner extends TestPartner {

    @XmlElement(required = true)
    public String getWSDLUrl() {
        return getPublishedURL() + "?wsdl";
    }

    @XmlElement(required = true)
    private final String url;

    @XmlElement(required = true)
    private final Path wsdl;

    WSDLTestPartner() {
        this("", Paths.get(""));
    }

    public WSDLTestPartner(String url, Path wsdl) {
        this.url = url;
        this.wsdl = wsdl;
    }

    @XmlElement(required = true)
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

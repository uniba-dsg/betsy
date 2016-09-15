package pebl.test.partner;

import java.nio.file.Path;

public class ExternalWSDLTestPartner implements WSDLTestPartner {

    public final String url;
    public final Path wsdl;

    public ExternalWSDLTestPartner(String url, Path wsdl) {
        this.url = url;
        this.wsdl = wsdl;
    }

    @Override
    public String getPublishedURL() {
        return url;
    }
}

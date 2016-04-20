package betsy.common.model.input;

import java.nio.file.Path;

public class ExternalTestPartner implements TestPartner {

    public final String url;
    public final Path wsdl;

    public ExternalTestPartner(String url, Path wsdl) {
        this.url = url;
        this.wsdl = wsdl;
    }

}

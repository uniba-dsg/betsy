package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class HttpOutput extends NoOutput {

    @XmlElement(required = true)
    private final int statusCode;

    @XmlElement(required = true)
    private final String mimetype;

    @XmlElement(required = true)
    @XmlCDATA
    private final String content;

    HttpOutput() {
        this(0,"", "");
    }

    public HttpOutput(int statusCode, String mimetype, String content) {
        this.statusCode = statusCode;
        this.mimetype = mimetype;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMimetype() {
        return mimetype;
    }
}

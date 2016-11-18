package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class HttpOutput extends NoOutput {

    @XmlAttribute(required = true)
    private final int statusCode;

    @XmlAttribute(required = true)
    private final String mimetype;

    @XmlValue
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

    public String getContent() {
        return content;
    }
}

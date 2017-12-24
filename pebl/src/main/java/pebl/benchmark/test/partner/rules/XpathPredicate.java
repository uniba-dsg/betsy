package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class XpathPredicate extends AnyInput {

    @XmlValue
    @XmlCDATA
    private final String xpathExpression;

    XpathPredicate() {
        this(String.valueOf(Integer.MIN_VALUE));
    }

    public XpathPredicate(String xpathExpression) {
        this.xpathExpression = xpathExpression;
    }

    public String getXpathExpression() {
        return xpathExpression;
    }
}

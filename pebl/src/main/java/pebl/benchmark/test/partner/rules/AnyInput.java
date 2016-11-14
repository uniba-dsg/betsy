package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({XpathPredicate.class, ScriptPredicate.class})
@XmlRootElement
public class AnyInput {

}

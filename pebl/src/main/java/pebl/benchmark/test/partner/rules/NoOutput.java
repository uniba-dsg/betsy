package pebl.benchmark.test.partner.rules;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({SoapMessageOutput.class, ScriptBasedOutput.class, SoapFaultOutput.class})
@XmlRootElement
public class NoOutput {

}

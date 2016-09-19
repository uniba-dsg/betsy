package pebl.test.partner.rules;

import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({EchoInputAsOutput.class, RawOutput.class, IntegerOutputWithStatusCode.class, IntegerOutputBasedOnScriptResult.class, IntegerOutput.class, FaultOutput.class})
public class Output {

}

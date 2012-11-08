package betsy.executables.ws

import de.uniba.wiai.dsg.bpel_engine_comparison.activities.wsdl.testpartner.TestPartnerPortType

import javax.jws.WebService
import javax.xml.soap.SOAPFactory
import javax.xml.soap.SOAPFault
import javax.xml.namespace.QName
import javax.xml.ws.soap.SOAPFaultException

@WebService(
name = "TestPartnerPortType",
serviceName = "TestPartnerService",
portName = "TestPartnerInterfacePortTypeBindingPort",
targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner",
endpointInterface = "de.uniba.wiai.dsg.bpel_engine_comparison.activities.wsdl.testpartner.TestPartnerPortType",
wsdlLocation = "TestPartner.wsdl")
class TestPartnerServiceMock implements TestPartnerPortType {

    private final boolean replyInput

    public TestPartnerServiceMock() {
        this.replyInput = true
    }


    public TestPartnerServiceMock(boolean replyInput) {
        this.replyInput = replyInput
    }

    public void startProcessAsync(int inputPart) {
        println "Partner: startProcessAsync with ${inputPart}"
    }

    public int startProcessSync(int inputPart) {
        println "Partner: startProcessSync with ${inputPart}"
        if (inputPart == -5) {
            SOAPFactory fac = SOAPFactory.newInstance();
            SOAPFault sf = fac.createFault("expected Error", new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner","CustomFault"))
            throw new SOAPFaultException(sf);
        }
        if (replyInput) {
            return inputPart
        } else {
            return 0
        }
    }

    public void startProcessWithEmptyMessage() {
        println "Partner: startProcessWithEmptyMessage"
    }


}

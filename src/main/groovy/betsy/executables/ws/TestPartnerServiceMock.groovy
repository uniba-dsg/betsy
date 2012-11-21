package betsy.executables.ws

import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType

import javax.jws.WebService
import javax.xml.soap.SOAPFactory
import javax.xml.soap.SOAPFault
import javax.xml.namespace.QName
import javax.xml.ws.soap.SOAPFaultException
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.ObjectFactory
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.FaultMessage

@WebService(
name = "TestPartnerPortType",
serviceName = "TestService",
portName = "TestPort",
targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner",
endpointInterface = "de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType",
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
            throw new SOAPFaultException(sf)
        }

        if (inputPart == -6) {
            FaultMessage fault = new FaultMessage("expected Error",inputPart)
            throw fault
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
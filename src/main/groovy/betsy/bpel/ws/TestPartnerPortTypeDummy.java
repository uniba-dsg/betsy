package betsy.bpel.ws;

import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.FaultMessage;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(
        name = "TestPartnerPortType",
        serviceName = "TestService",
        portName = "TestPort",
        targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner",
        endpointInterface = "de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType",
        wsdlLocation = "TestPartner.wsdl")
public class TestPartnerPortTypeDummy implements TestPartnerPortType {

    @Override
    public void startProcessAsync(@WebParam(name = "testElementAsyncRequest", targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", partName = "inputPart") int inputPart) {
        // do nothing
    }

    @Override
    public int startProcessSync(@WebParam(name = "testElementSyncRequest", targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", partName = "inputPart") int inputPart) throws FaultMessage {
        return 0;
    }

    @Override
    public void startProcessWithEmptyMessage() {
        // do nothing
    }
}

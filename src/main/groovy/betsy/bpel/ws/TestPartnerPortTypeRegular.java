package betsy.bpel.ws;

import betsy.bpel.model.ConcurrencyDetectionCodes;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.FaultMessage;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType;
import org.apache.log4j.Logger;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.Date;

@WebService(name = "TestPartnerPortType", serviceName = "TestService", portName = "TestPort", targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", endpointInterface = "de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType", wsdlLocation = "TestPartner.wsdl")
public class TestPartnerPortTypeRegular implements TestPartnerPortType {

    private static final Logger log = Logger.getLogger(TestPartnerPortTypeRegular.class);

    public static final int CODE_THROW_CUSTOM_FAULT = -5;
    public static final int CODE_THROW_FAULT = -6;

    private final ConcurrencyDetector detector = new ConcurrencyDetector();

    private static void logInfo(final String message) {
        log.info("[" + new Date() + "] " + message);
    }

    private static void logInfo(final String message, final Exception e) {
        log.info("[" + new Date() + "] " + message, e);
    }

    public void startProcessAsync(final int inputPart) {
        logInfo("Partner: startProcessAsync with " + inputPart);
    }

    public int startProcessSync(final int inputPart) throws FaultMessage {
        String logHeader = "Partner: startProcessSync with input " + inputPart;
        logInfo(logHeader);


        if (inputPart == CODE_THROW_CUSTOM_FAULT) {
            logInfo(logHeader + " - Throwing CustomFault");
            try {
                SOAPFactory fac = SOAPFactory.newInstance();
                SOAPFault sf = fac.createFault("expected Error", new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server"));
                Detail detail = sf.addDetail();
                detail.addDetailEntry(new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", "Error"));
                throw new SOAPFaultException(sf);
            } catch (SOAPException e) {
                logInfo("Exception occurred during building the response message", e);
                throw new RuntimeException("could not create response", e);
            }
        } else if (inputPart == CODE_THROW_FAULT) {
            logInfo(logHeader + " - Throwing Fault");
            throw new FaultMessage("expected Error", inputPart);
        } else {
            final int result = detectConcurrency(inputPart);
            logInfo(logHeader + " - Returning " + result);
            return result;
        }
    }

    private int detectConcurrency(final int inputPart) {
        if (inputPart == ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___OPERATION_UNDER_TEST) {
            return detector.access();
        } else if (inputPart == ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_CONCURRENT_ACCESS) {
            return detector.getNumberOfConcurrentCalls();
        } else if (inputPart == ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___GET_TOTAL_ACCESSES) {
            return detector.getNumberOfCalls();
        } else if (inputPart == ConcurrencyDetectionCodes.CODE_CONCURRENCY_DETECTION___RESET_COUNTERS) {
            return detector.reset();
        } else {
            return inputPart;
        }
    }



    public void startProcessWithEmptyMessage() {
        logInfo("Partner: startProcessWithEmptyMessage");
    }
}

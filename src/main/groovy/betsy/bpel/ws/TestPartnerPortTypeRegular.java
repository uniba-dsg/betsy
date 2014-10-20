package betsy.bpel.ws;

import betsy.common.tasks.WaitTasks;
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
import java.util.concurrent.atomic.AtomicInteger;

@WebService(name = "TestPartnerPortType", serviceName = "TestService", portName = "TestPort", targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", endpointInterface = "de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType", wsdlLocation = "TestPartner.wsdl")
public class TestPartnerPortTypeRegular implements TestPartnerPortType {
    public static final int CONCURRENCY_TIMEOUT = 1000;
    private static final Logger log = Logger.getLogger(TestPartnerPortTypeRegular.class);
    private final boolean replyInput;
    private final AtomicInteger concurrentAccesses = new AtomicInteger(0);
    private final AtomicInteger totalConcurrentAccesses = new AtomicInteger(0);
    private final AtomicInteger totalAccesses = new AtomicInteger(0);

    public TestPartnerPortTypeRegular(boolean replyInput) {
        this.replyInput = replyInput;
    }

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
        logInfo("Partner: startProcessSync with " + inputPart);
        totalAccesses.incrementAndGet();

        if (inputPart == -5) {
            logInfo("Partner: startProcessSync with " + inputPart + " - Throwing CustomFault");
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
        } else if (inputPart == -6) {
            logInfo("Partner: startProcessSync with " + inputPart + " - Throwing Fault");
            throw new FaultMessage("expected Error", inputPart);
        } else if (replyInput) {
            return testWithConcurrency(inputPart);
        } else {
            return 0;
        }

    }

    private int testWithConcurrency(final int inputPart) {
        if (inputPart == 100) {
            //magic number for tracking concurrent accesses
            concurrentAccesses.incrementAndGet();

            WaitTasks.sleep(CONCURRENCY_TIMEOUT);

            int result = 100;
            if (concurrentAccesses.get() == 1) {
                // no concurrency detected
                result = 0;
            } else {
                logInfo("Partner: startProcessSync with " + inputPart + " - Concurrency Detected");
                totalConcurrentAccesses.incrementAndGet();
            }

            concurrentAccesses.decrementAndGet();

            logInfo("Partner: startProcessSync with " + inputPart + " - Returning " + result);

            return result;
        } else if (inputPart == 101) {
            // magic number for querying number of concurrent accesses, do not count to totalAccesses
            totalAccesses.decrementAndGet();
            //reset totalConcurrentAccesses after each query
            final int result = totalConcurrentAccesses.get();
            totalConcurrentAccesses.set(0);

            logInfo("Partner: startProcessSync with " + inputPart + " - Returning " + result);

            return result;
        } else if (inputPart == 102) {
            // magic number for querying number of total accesses, do not count to totalAccesses
            final int result = totalAccesses.decrementAndGet();
            //reset totalAccesses after each query
            totalAccesses.set(0);

            logInfo("Partner: startProcessSync with " + inputPart + " - Returning " + result);

            return result;
        } else {
            return inputPart;
        }

    }

    public void startProcessWithEmptyMessage() {
        logInfo("Partner: startProcessWithEmptyMessage");
    }
}

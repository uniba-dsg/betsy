package betsy.executables.ws

import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType

import javax.jws.WebService
import javax.xml.soap.SOAPFactory
import javax.xml.soap.SOAPFault
import javax.xml.namespace.QName
import javax.xml.ws.soap.SOAPFaultException

import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.FaultMessage
import java.util.concurrent.atomic.AtomicInteger

@WebService(
name = "TestPartnerPortType",
serviceName = "TestService",
portName = "TestPort",
targetNamespace = "http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner",
endpointInterface = "de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.TestPartnerPortType",
wsdlLocation = "TestPartner.wsdl")
class TestPartnerServiceMock implements TestPartnerPortType {

    public static final int CONCURRENCY_TIMEOUT = 1000

    private final boolean replyInput

    private final AtomicInteger concurrentAccesses = new AtomicInteger(0)

    private final AtomicInteger totalConcurrentAccesses = new AtomicInteger(0)

    private final AtomicInteger totalAccesses = new AtomicInteger(0)

    public TestPartnerServiceMock(boolean replyInput) {
        this.replyInput = replyInput
    }

    public void startProcessAsync(int inputPart) {
        println "Partner: startProcessAsync with ${inputPart}"
    }

    public int startProcessSync(int inputPart) {
        println "[${new Date()}] Partner: startProcessSync with ${inputPart}"
        totalAccesses.incrementAndGet()

        if (inputPart == -5) {
            println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Throwing CustomFault"

            SOAPFactory fac = SOAPFactory.newInstance();
            SOAPFault sf = fac.createFault("expected Error", new QName("http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner", "CustomFault"))
            throw new SOAPFaultException(sf)
        }

        if (inputPart == -6) {
            println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Throwing Fault"
            FaultMessage fault = new FaultMessage("expected Error", inputPart)
            throw fault
        }

        if (replyInput) {
            return testWithConcurrency(inputPart)
        } else {
            return 0
        }
    }

    private int testWithConcurrency(int inputPart) {
        if (inputPart == 100) {
            //magic number for tracking concurrent accesses
            concurrentAccesses.incrementAndGet()
            Thread.sleep(CONCURRENCY_TIMEOUT)

            int result = 100
            if (concurrentAccesses.get() == 1) {
                // no concurrency detected
                result = 0
            } else {
                println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Concurrency Detected"
                totalConcurrentAccesses.incrementAndGet()
            }
            concurrentAccesses.decrementAndGet()

            println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Returning ${result}"

            return result
        } else if (inputPart == 101) {
            // magic number for querying number of concurrent accesses, do not count to totalAccesses
            totalAccesses.decrementAndGet()
            //reset totalConcurrentAccesses after each query
            int result = totalConcurrentAccesses.get()
            totalConcurrentAccesses.set(0)

            println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Returning ${result}"

            return result
        } else if (inputPart == 102) {
            // magic number for querying number of total accesses, do not count to totalAccesses
            int result = totalAccesses.decrementAndGet()
            //reset totalAccesses after each query
            totalAccesses.set(0)

            println "[${new Date()}] Partner: startProcessSync with ${inputPart} - Returning ${result}"

            return result
        } else {
            return inputPart
        }
    }

    public void startProcessWithEmptyMessage() {
        println "[${new Date()}] Partner: startProcessWithEmptyMessage"
    }

}
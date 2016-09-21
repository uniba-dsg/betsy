package configuration.bpel;

import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.JAXB;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import betsy.bpel.model.BPELTestCase;
import betsy.bpel.ws.TestPartnerPortTypeRegular;
import betsy.common.util.FileTypes;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import de.uniba.wiai.dsg.betsy.activities.wsdl.testpartner.FaultMessage;
import pebl.feature.Feature;
import pebl.feature.FeatureSet;
import pebl.test.Test;
import pebl.test.partner.InternalWSDLTestPartner;
import pebl.test.partner.rules.AnyInput;
import pebl.test.partner.rules.EchoInputAsOutput;
import pebl.test.partner.rules.IntegerInput;
import pebl.test.partner.rules.IntegerOutputBasedOnScriptResult;
import pebl.test.partner.rules.OperationInputOutputRule;
import pebl.test.partner.rules.SoapFaultOutput;

public class BPELProcessBuilder {

    public static String getExpectedSoapFault() {
        return "<S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">\n"
                + "            <faultcode>S:Server</faultcode>\n"
                + "            <faultstring>expected Error</faultstring>\n"
                + "            <detail>\n"
                + "                <testElementFault xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner\">-6</testElementFault>\n"
                + "            </detail>\n"
                + "        </S:Fault>";
        /*
            FaultMessage jaxbObject = new FaultMessage("expected Error", -6);
        */
    }

    public static String getSoapFault() {
        /*
        "<S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">\n"
                + "            <faultcode>S:Server</faultcode>\n"
                + "            <faultstring>expected Error</faultstring>\n"
                + "            <detail>\n"
                + "                <Error xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"/>\n"
                + "            </detail>\n"
                + "        </S:Fault>"
        */
        try {
            SOAPFault soapFault = TestPartnerPortTypeRegular.createSoapFault();
            Message soapFaultMessage = SOAPFaultBuilder.createSOAPFaultMessage(SOAPVersion.SOAP_11, soapFault);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            soapFaultMessage.writeTo(writer);
            return out.toString();
        } catch (XMLStreamException e) {
            return "";
        }
    }

    public static final Path PATH_PREFIX = Paths.get("src/main/tests/files/bpel");
    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
    public static final InternalWSDLTestPartner DUMMY_TEST_PARTNER = new InternalWSDLTestPartner(
            Paths.get("TestPartner.wsdl"), "http://localhost:2000/bpel-assigned-testpartner");
    public static final InternalWSDLTestPartner REGULAR_TEST_PARTNER = new InternalWSDLTestPartner(
            Paths.get("TestPartner.wsdl"),
            "http://localhost:2000/bpel-testpartner",
            new OperationInputOutputRule("startProcessAsync", new AnyInput()),
            new OperationInputOutputRule("startProcessWithEmptyMessage", new AnyInput()),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(-5), new SoapFaultOutput(getSoapFault())),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(-6), new SoapFaultOutput(getExpectedSoapFault())),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(100), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.access()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(101), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfConcurrentCalls()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(102), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.getNumberOfCalls()")),
            new OperationInputOutputRule("startProcessSync", new IntegerInput(103), new IntegerOutputBasedOnScriptResult("ConcurrencyDetector.reset()")),
            new OperationInputOutputRule("startProcessSync", new AnyInput(), new EchoInputAsOutput())
    );

    public static Test buildPatternProcess(final String name, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildPatternProcessWithPartner(final String name, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER));
    }

    public static Test buildStructuredActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildScopeProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildScopeProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Collections.singletonList(testInterface), Collections.emptyList());
    }

    public static Test buildBasicProcessWithXsd(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/months.xsd")), Collections.emptyList());
    }

    public static Test buildBasicProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases), feature, Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicProcessWithRegularAndDummyPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface),
                Arrays.asList(REGULAR_TEST_PARTNER, DUMMY_TEST_PARTNER));
    }

    public static Test buildErrorProcessWithPartner(String constructName, String name, String description, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("errorsbase").resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                new Feature(new FeatureSet(Groups.ERROR, constructName), name),
                Arrays.asList(testInterface, partnerInterface), Arrays.asList(ErrorProcesses.ERROR_TEST_PARTNER));
    }

    public static Test buildStructuredProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER));
    }

    public static Test buildBasicProcessWithXslt(String name, String description, Feature feature, BPELTestCase... testCases) {
        return new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt")), Collections.emptyList());
    }

}

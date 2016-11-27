package configuration.bpel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.TestMessages;
import betsy.common.util.FileTypes;
import configuration.Capabilities;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.test.Test;
import pebl.benchmark.test.partner.RuleBasedWSDLTestPartner;
import pebl.benchmark.test.partner.rules.AnyInput;
import pebl.benchmark.test.partner.rules.NoOutput;
import pebl.benchmark.test.partner.rules.OperationInputOutputRule;
import pebl.benchmark.test.partner.rules.ScriptBasedOutput;
import pebl.benchmark.test.partner.rules.SoapFaultOutput;
import pebl.benchmark.test.partner.rules.XpathPredicate;

import static configuration.FilesLocation.BPEL_LOCATION;

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
        return "<S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">\n"
                + "            <faultcode>S:Server</faultcode>\n"
                + "            <faultstring>expected Error</faultstring>\n"
                + "            <detail>\n"
                + "                <Error xmlns=\"http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"/>\n"
                + "            </detail>\n"
                + "        </S:Fault>";
    }

    public static final Path PATH_PREFIX = Paths.get(BPEL_LOCATION);

    public static final Path testInterface = PATH_PREFIX.resolve("TestInterface.wsdl");
    public static final Path partnerInterface = PATH_PREFIX.resolve("TestPartner.wsdl");
    public static final int UNDECLARED_FAULT = -5;
    public static final int DECLARED_FAULT = -6;
    public static final RuleBasedWSDLTestPartner DUMMY_TEST_PARTNER = new RuleBasedWSDLTestPartner(
            Paths.get("TestPartner.wsdl"), "http://localhost:2000/bpel-assigned-testpartner");
    public static final RuleBasedWSDLTestPartner REGULAR_TEST_PARTNER = new RuleBasedWSDLTestPartner(
            Paths.get("TestPartner.wsdl"),
            "http://localhost:2000/bpel-testpartner",
            new OperationInputOutputRule("startProcessAsync", new AnyInput()),
            new OperationInputOutputRule("startProcessWithEmptyMessage", new AnyInput()),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + -5),
                    new SoapFaultOutput(getSoapFault())),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + -6), new SoapFaultOutput(getExpectedSoapFault())),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + 100), new ScriptBasedOutput("ConcurrencyDetector.access()")),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + 101), new ScriptBasedOutput("ConcurrencyDetector.getNumberOfConcurrentCalls()")),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + 102), new ScriptBasedOutput("ConcurrencyDetector.getNumberOfCalls()")),
            new OperationInputOutputRule("startProcessSync", new XpathPredicate("declare namespace test='http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner';//test:testElementSyncRequest = " + 103), new ScriptBasedOutput("ConcurrencyDetector.reset()")),
            new OperationInputOutputRule("startProcessSync", new AnyInput(), new NoOutput()) // TODO should be echo, but not possible
    );

    public static Test buildPatternProcess(final String name, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases),
                feature,
                Collections.singletonList(testInterface), Collections.emptyList()));
    }

    public static Test buildPatternProcessWithPartner(final String name, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("cfpatterns/" + name + FileTypes.BPEL), "", Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER)));
    }

    public static Test buildStructuredActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Collections.singletonList(testInterface), Collections.emptyList()));
    }

    public static Test buildScopeProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Collections.singletonList(testInterface), Collections.emptyList()));
    }

    public static Test buildScopeProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("scopes/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER)));
    }

    public static Test buildBasicActivityProcess(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Collections.singletonList(testInterface), Collections.emptyList()));
    }

    public static Test buildBasicProcessWithXsd(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/months.xsd")), Collections.emptyList()));
    }

    public static Test buildBasicProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL), description, Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER)));
    }

    public static Test buildBasicProcessWithRegularAndDummyPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface),
                Arrays.asList(REGULAR_TEST_PARTNER, DUMMY_TEST_PARTNER)));
    }

    public static Test buildErrorProcessWithPartner(String constructName, String name, String description, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("errorsbase").resolve(name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                // feature is only dummy, will be replaced later on, as a test template is created here
                new Feature(new FeatureSet(Groups.DUMMY, constructName), name),
                Arrays.asList(testInterface, partnerInterface), Arrays.asList(ErrorProcesses.ERROR_TEST_PARTNER)));
    }

    public static Test buildStructuredProcessWithPartner(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("structured/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, partnerInterface), Collections.singletonList(REGULAR_TEST_PARTNER)));
    }

    public static Test buildBasicProcessWithXslt(String name, String description, Feature feature, BPELTestCase... testCases) {
        return Capabilities.addMetrics(new Test(PATH_PREFIX.resolve("basic/" + name + FileTypes.BPEL),
                description,
                Arrays.asList(testCases),
                feature,
                Arrays.asList(testInterface, PATH_PREFIX.resolve("basic/echo.xslt"), PATH_PREFIX.resolve("basic/notCompileable.xslt")), Collections.emptyList()));
    }

}

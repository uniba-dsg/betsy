package betsy.bpel.soapui;

import pebl.test.TestStep;
import pebl.test.assertions.ExitAssertion;
import pebl.test.assertions.SoapFaultTestAssertion;
import pebl.test.assertions.XpathTestAssertion;
import pebl.test.steps.soap.SoapTestStep;
import pebl.test.TestAssertion;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.GroovyScriptAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleContainsAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.NotSoapFaultAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory;

import java.util.Objects;

public class SoapUiAssertionBuilder {
    public static void addSynchronousAssertion(SoapTestStep testStep, WsdlTestRequestStep soapUiRequest, WsdlTestCase soapUITestCase, int testStepNumber) {
        for (TestAssertion assertion : testStep.getAssertions()) {

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, (XpathTestAssertion) assertion);
            } else if (assertion instanceof SoapFaultTestAssertion) {
                addSoapFaultTestAssertion(soapUiRequest, (SoapFaultTestAssertion) assertion);
            } else if (assertion instanceof ExitAssertion) {
                addExitAssertion(soapUITestCase, testStepNumber);
            }

        }

        createNotSoapFaultAssertion(testStep, soapUiRequest);

    }

    private static void createNotSoapFaultAssertion(TestStep testStep, WsdlTestRequestStep soapUiRequest) {
        if (!testStep.getAssertions().stream().anyMatch((it) -> it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion)) {
            Objects.requireNonNull(soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL), "Could not create Not Soap Fault Assertion");
        }
    }

    public static void addSoapFaultTestAssertion(WsdlTestRequestStep soapUiRequest, final SoapFaultTestAssertion assertion) {
        // validate result
        //Objects.requireNonNull(soapUiRequest.addAssertion(SoapResponseAssertion.LABEL), "Could not create Soap Response Assertion")
        Objects.requireNonNull(soapUiRequest.addAssertion(SoapFaultAssertion.LABEL), "Could not create Soap Fault Assertion");

        if (assertion.getFaultString() != null) {
            SimpleContainsAssertion simpleContainsAssertion = (SimpleContainsAssertion) soapUiRequest.addAssertion(SimpleContainsAssertion.LABEL);
            Objects.requireNonNull(simpleContainsAssertion, "Simple contains assertion could not be created for " + String.valueOf(assertion));

            simpleContainsAssertion.setToken(assertion.getFaultString());
            simpleContainsAssertion.setIgnoreCase(false);
        }

    }

    public static void addExitAssertion(WsdlTestCase soapUITestCase, int testStepNumber) {

        WsdlGroovyScriptTestStep groovyScriptTestStep = (WsdlGroovyScriptTestStep) soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Socket Timeout for Step #" + String.valueOf(testStepNumber));
        groovyScriptTestStep.setScript("\n" +
                "try {\n" +
                "    def messageExchange = testRunner.results.last()\n" +
                "\n" +
                "    if(\n" +
                "        ( !messageExchange.hasResponse() ) ||\n" +
                "        ( messageExchange.hasResponse() && messageExchange.responseStatusCode == 200 && messageExchange.responseContent == null ) ||\n" +
                "        ( messageExchange.hasResponse() && messageExchange.responseStatusCode == 500) ||\n" +
                "        ( messageExchange.hasResponse() && messageExchange.responseContent.contains(\"processTerminated\") ) ||\n" +
                "        ( messageExchange.hasResponse() && messageExchange.responseContent.contains(\"terminating the process instance\") ) ||\n" +
                "        ( messageExchange.hasResponse() && messageExchange.responseContent.contains(\"process instance is being terminated\") )\n" +
                "       ) {\n" +
                "        log.info(\"EXIT OK - no correct response\")\n" +
                "    } else {\n" +
                "        testRunner.fail(\"process instance still active\")\n" +
                "    }\n" +
                "} catch (Exception e) {\n" +
                "    testRunner.fail(\"exception with message \\${e.message}\")\n" +
                "}\n");
        soapUITestCase.setFailOnError(false);
        soapUITestCase.setFailTestCaseOnErrors(false);
    }

    public static void addOneWayAssertion(WsdlTestRequest soapUiRequest) {
        // is one way
        GroovyScriptAssertion groovyScriptAssertion = (GroovyScriptAssertion) soapUiRequest.addAssertion(GroovyScriptAssertion.LABEL);
        groovyScriptAssertion.setScriptText("assert 202 == messageExchange.responseStatusCode");
    }

    public static void addTestPartnerAssertion(TestStep testStep, WsdlTestRequestStep soapUiRequest) {
        for (TestAssertion assertion : testStep.getAssertions()) {
            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, (XpathTestAssertion) assertion);
            }
        }

        createNotSoapFaultAssertion(testStep, soapUiRequest);

    }

    public static void addXpathTestAssertion(WsdlTestRequestStep soapUiRequest, XpathTestAssertion assertion) {
        // validate result
        Objects.requireNonNull(soapUiRequest.addAssertion(SoapResponseAssertion.LABEL), "Could not create Soap Response Assertion");

        XPathContainsAssertion xPathContainsAssertion = (XPathContainsAssertion) soapUiRequest.addAssertion(XPathContainsAssertion.LABEL);
        xPathContainsAssertion.setPath(assertion.getXpathExpression());
        xPathContainsAssertion.setExpectedContent(assertion.getExpectedOutput());
    }

}

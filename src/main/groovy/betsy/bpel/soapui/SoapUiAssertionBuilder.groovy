package betsy.bpel.soapui

import betsy.common.model.steps.SoapTestStep
import betsy.common.model.TestAssertion
import betsy.common.model.TestStep
import betsy.common.model.assertions.ExitAssertion
import betsy.common.model.assertions.SoapFaultTestAssertion
import betsy.common.model.assertions.XpathTestAssertion
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.GroovyScriptAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.NotSoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory

class SoapUiAssertionBuilder {

    public static void addSynchronousAssertion(SoapTestStep testStep, WsdlTestRequestStep soapUiRequest, WsdlTestCase soapUITestCase, int testStepNumber) {
        for (TestAssertion assertion : testStep.assertions) {

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, assertion)
            } else if (assertion instanceof SoapFaultTestAssertion) {
                addSoapFaultTestAssertion(soapUiRequest, assertion)
            } else if (assertion instanceof ExitAssertion) {
                addExitAssertion(soapUITestCase, testStepNumber)
            }
        }

        if (!testStep.assertions.any { it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion }) {
            Objects.requireNonNull(soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL), "Could not create not soap fault assertion")
        }
    }

    public static void addSoapFaultTestAssertion(WsdlTestRequestStep soapUiRequest, SoapFaultTestAssertion assertion) {
        // validate result
        //Objects.requireNonNull(soapUiRequest.addAssertion(SoapResponseAssertion.LABEL), "Could not create Soap Response Assertion")
        Objects.requireNonNull(soapUiRequest.addAssertion(SoapFaultAssertion.LABEL), "Could not create Soap Fault Assertion")

        if (assertion.faultString != null) {
            SimpleContainsAssertion simpleContainsAssertion = soapUiRequest.addAssertion(SimpleContainsAssertion.LABEL) as SimpleContainsAssertion
            Objects.requireNonNull(simpleContainsAssertion, "Simple contains assertion could not be created for ${assertion}")

            simpleContainsAssertion.token = assertion.faultString
            simpleContainsAssertion.ignoreCase = false
        }
    }

    public static void addExitAssertion(WsdlTestCase soapUITestCase, int testStepNumber) {

        WsdlGroovyScriptTestStep groovyScriptTestStep = soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Socket Timeout for Step #$testStepNumber") as WsdlGroovyScriptTestStep
        groovyScriptTestStep.script = """
try {
    def messageExchange = testRunner.results.last()

    if(
        ( !messageExchange.hasResponse() ) ||
        ( messageExchange.hasResponse() && messageExchange.responseStatusCode == 200 && messageExchange.responseContent == null ) ||
        ( messageExchange.hasResponse() && messageExchange.responseStatusCode == 500) ||
        ( messageExchange.hasResponse() && messageExchange.responseContent.contains("processTerminated") ) ||
        ( messageExchange.hasResponse() && messageExchange.responseContent.contains("terminating the process instance") ) ||
        ( messageExchange.hasResponse() && messageExchange.responseContent.contains("process instance is being terminated") )
       ) {
        log.info("EXIT OK - no correct response")
    } else {
        testRunner.fail("process instance still active")
    }
} catch (Exception e) {
    testRunner.fail("exception with message \${e.message}")
}
"""
        soapUITestCase.failOnError = false
        soapUITestCase.failTestCaseOnErrors = false
    }

    public static void addOneWayAssertion(WsdlTestRequest soapUiRequest) {
        // is one way
        GroovyScriptAssertion groovyScriptAssertion = soapUiRequest.addAssertion(GroovyScriptAssertion.LABEL) as GroovyScriptAssertion
        groovyScriptAssertion.scriptText = "assert 202 == messageExchange.responseStatusCode"
    }



    public static void addTestPartnerAssertion(TestStep testStep, WsdlTestRequestStep soapUiRequest) {
        for (TestAssertion assertion : testStep.assertions) {
            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, assertion)
            }
        }

        if (!testStep.assertions.any { it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion }) {
            Objects.requireNonNull(soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL), "Could not create Not Soap Fault Assertion")
        }
    }

    public static void addXpathTestAssertion(WsdlTestRequestStep soapUiRequest, XpathTestAssertion assertion) {
        // validate result
        Objects.requireNonNull(soapUiRequest.addAssertion(SoapResponseAssertion.LABEL), "Could not create Soap Response Assertion");

        XPathContainsAssertion xPathContainsAssertion = soapUiRequest.addAssertion(XPathContainsAssertion.LABEL) as XPathContainsAssertion
        xPathContainsAssertion.path = assertion.xpathExpression
        xPathContainsAssertion.expectedContent = assertion.expectedOutput
    }
}

package betsy.executables.soapui.builder

import betsy.data.TestStep
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.XpathTestAssertion
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


class SoapUIAssertionBuilder {

    public static void addSynchronousAssertion(TestStep testStep, WsdlTestRequest soapUiRequest, WsdlTestCase soapUITestCase, WsdlTestRequestStep soapUiRequestStep, int testStepNumber) {
        testStep.assertions.each {assertion ->

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof SoapFaultTestAssertion) {
                addSoapFaultTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof ExitAssertion) {
                addExitAssertion(soapUITestCase, testStepNumber)
            }
        }

        if (!testStep.assertions.any {it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion}) {
            soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL)
        }
    }

    public static void addSoapFaultTestAssertion(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, SoapFaultTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)
        soapUiRequest.addAssertion(SoapFaultAssertion.LABEL)

        if (assertion.faultString) {
            SimpleContainsAssertion simpleContainsAssertion = soapUiRequestStep.addAssertion(SimpleContainsAssertion.LABEL) as SimpleContainsAssertion
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



    public static void addTestPartnerAssertion(TestStep testStep, WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep) {
        testStep.assertions.each {assertion ->

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            }
        }

        if (!testStep.assertions.any {it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion}) {
            soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL)
        }
    }

    public static void addXpathTestAssertion(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, XpathTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)

        XPathContainsAssertion xPathContainsAssertion = soapUiRequestStep.addAssertion(XPathContainsAssertion.LABEL) as XPathContainsAssertion
        xPathContainsAssertion.path = assertion.xpathExpression
        xPathContainsAssertion.expectedContent = assertion.expectedOutput
    }
}

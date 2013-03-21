package betsy.executables.soapui

import betsy.data.TestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest
import betsy.data.TestCase
import betsy.data.Process
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.config.TestStepConfig
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep
import com.eviware.soapui.impl.wsdl.WsdlProject
import betsy.data.WsdlOperation
import betsy.data.assertions.XpathTestAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.ExitAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.NotSoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.GroovyScriptAssertion
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.DelayStepFactory

class SoapUiTestStepBuilder {

    private TestCase testCase

    private WsdlTestCase soapUiTestCase

    private WsdlProject wsdlProject

    private Process process

    private int requestTimeout

    public SoapUiTestStepBuilder(TestCase testCase, WsdlTestCase soapUiTestCase, WsdlProject wsdlProject, Process process, int requestTimeout) {
        this.testCase = testCase
        this.soapUiTestCase = soapUiTestCase
        this.wsdlProject = wsdlProject
        this.process = process
        this.requestTimeout = requestTimeout
    }


    public void addTestStep(TestStep testStep) {
        int testStepNumber = testCase.testSteps.indexOf(testStep)

        if (testStep.isTestPartner()) {
            addStepForTestPartner(testStep, testStepNumber)
        } else {
            addStepForTestInterface(testStep, testStepNumber)
        }
    }

    public void addStepForTestPartner(TestStep testStep, int testStepNumber) {
        WsdlTestRequestStep partnerRequestStep = createTestStepConfig(soapUiTestCase, testStep, testStepNumber, "TestPartnerPortTypeBinding","startProcessSync")
        WsdlTestRequest soapUiRequest = createSoapUiRequest(partnerRequestStep, testStep)
        addTestPartnerAssertion(testStep, soapUiRequest, partnerRequestStep)
    }

    private WsdlTestRequestStep createTestStepConfig(WsdlTestCase soapUiTestCase, TestStep testStep, int testStepNumber, String portTypeName, String operationName) {
        String wsdlOperationName = "Sending ${portTypeName}.${operationName} Step #${testStepNumber}"

        WsdlInterface wsdlInterface = wsdlProject.getInterfaceByName(portTypeName) as WsdlInterface
        com.eviware.soapui.impl.wsdl.WsdlOperation op = wsdlInterface.getOperationByName(operationName)

        if (op == null) {
            throw new RuntimeException("WsdlOperation ${portTypeName}.${operationName} could not be found in soapUI project of process ${process} on engine ${process.engine}")
        }

        TestStepConfig config = WsdlTestRequestStepFactory.createConfig(op, wsdlOperationName)

        if (config == null) {
            throw new RuntimeException("Could not create config for ${wsdlOperationName} of process ${process} on engine ${process.engine}")
        }

        WsdlTestStep soapUiTestStep = soapUiTestCase.addTestStep(config)

        if (soapUiTestStep == null) {
            throw new RuntimeException("Could not create request step for ${wsdlOperationName} of process ${process} on engine ${process.engine}")
        }

        soapUiTestStep as WsdlTestRequestStep
    }

    private WsdlTestRequest createSoapUiRequest(WsdlTestRequestStep soapUiRequestStep, TestStep testStep) {
        WsdlTestRequest soapUiRequest = soapUiRequestStep.testRequest
        if (testStep.operation.equals(WsdlOperation.SYNC)) {
            soapUiRequest.requestContent = createSyncInputMessage(testStep.input)
        } else if (testStep.operation.equals(WsdlOperation.ASYNC)) {
            soapUiRequest.requestContent = createAsyncInputMessage(testStep.input)
        } else if (testStep.isTestPartner()) {
            soapUiRequest.requestContent = createSyncTestPartnerInputMessage(testStep.input)
        } else {
            soapUiRequest.requestContent = createSyncStringInputMessage(testStep.input)
        }
        soapUiRequest.timeout = requestTimeout
        return soapUiRequest
    }

    private String createSyncTestPartnerInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testpartner">${input}</testElementSyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    private String createSyncInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementSyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    private String createSyncStringInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementSyncStringRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementSyncStringRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    private String createAsyncInputMessage(String input) {
        """
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <testElementAsyncRequest xmlns="http://dsg.wiai.uniba.de/betsy/activities/wsdl/testinterface">${input}</testElementAsyncRequest>
   </soapenv:Body>
</soapenv:Envelope>
        """
    }

    private void addTestPartnerAssertion(TestStep testStep, WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep) {
        testStep.assertions.each {assertion ->

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            }
        }

        if (!testStep.assertions.any {it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion}) {
            soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL)
        }
    }

    private void addXpathTestAssertion(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, XpathTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)

        XPathContainsAssertion xPathContainsAssertion = soapUiRequestStep.addAssertion(XPathContainsAssertion.LABEL) as XPathContainsAssertion
        xPathContainsAssertion.path = assertion.xpathExpression
        xPathContainsAssertion.expectedContent = assertion.expectedOutput
    }


    public void addStepForTestInterface(TestStep testStep, int testStepNumber) {
        WsdlTestRequestStep soapUiRequestStep = createTestStepConfig(soapUiTestCase, testStep, testStepNumber, "TestInterfacePortTypeBinding", testStep.operation.name)

        WsdlTestRequest soapUiRequest = createSoapUiRequest(soapUiRequestStep, testStep)

        if (!testStep.isOneWay()) {
            addSynchronousAssertion(testStep, soapUiRequest, soapUiTestCase, soapUiRequestStep, testStepNumber)
        } else {
            addOneWayAssertion(soapUiRequest)
        }

        if (testStep.timeToWaitAfterwards != null) {
            addDelayTime(soapUiTestCase, testStep, testStepNumber)
        }
    }

    private void addSynchronousAssertion(TestStep testStep, WsdlTestRequest soapUiRequest, WsdlTestCase soapUITestCase, WsdlTestRequestStep soapUiRequestStep, int testStepNumber) {
        testStep.assertions.each {assertion ->

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof SoapFaultTestAssertion) {
                addSoapFaultTestAssertion(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof ExitAssertion) {
                addExitAssertion(soapUiRequest, soapUiRequestStep, assertion, soapUITestCase, testStepNumber)
            }
        }

        if (!testStep.assertions.any {it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion}) {
            soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL)
        }
    }

    private void addSoapFaultTestAssertion(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, SoapFaultTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)
        soapUiRequest.addAssertion(SoapFaultAssertion.LABEL)

        if (assertion.faultString) {
            SimpleContainsAssertion simpleContainsAssertion = soapUiRequestStep.addAssertion(SimpleContainsAssertion.LABEL) as SimpleContainsAssertion
            simpleContainsAssertion.token = assertion.faultString
            simpleContainsAssertion.ignoreCase = false
        }
    }

    private void addExitAssertion(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, ExitAssertion exitAssertion, WsdlTestCase soapUITestCase, int testStepNumber) {

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

    private void addOneWayAssertion(WsdlTestRequest soapUiRequest) {
        // is one way
        GroovyScriptAssertion groovyScriptAssertion = soapUiRequest.addAssertion(GroovyScriptAssertion.LABEL) as GroovyScriptAssertion
        groovyScriptAssertion.scriptText = "assert 202 == messageExchange.responseStatusCode"
    }

    private void addDelayTime(WsdlTestCase soapUITestCase, TestStep testStep, int testStepNumber) {
        WsdlDelayTestStep delay = soapUITestCase.addTestStep(DelayStepFactory.DELAY_TYPE, "Delay for Step #$testStepNumber") as WsdlDelayTestStep
        delay.setDelay(testStep.timeToWaitAfterwards)
    }
}

package betsy.executables.soapui

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.XpathTestAssertion
import com.eviware.soapui.config.TestStepConfig
import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlProjectFactory
import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.GroovyScriptAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.NotSoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion
import com.eviware.soapui.impl.wsdl.teststeps.registry.DelayStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.*

/**
 * Creates a soapUI project from a Process.
 *
 * Not Thread-Safe!
 */
class SoapUiWrapper {

    final WsdlProjectFactory projectFactory = new WsdlProjectFactory()
    final WsdlInterfaceFactory interfaceFactory = new WsdlInterfaceFactory()

    Process process
    int requestTimeout

    private WsdlProject project
    private WsdlTestSuite soapUiTestSuite

    void createSoapUIProject() {
        createProject()
        importWsdlFiles()
        createTestCases()
        saveProject()
    }

    private void createProject() {
        project = projectFactory.createNew()
        project.name = "soapui" // this is also the top level package for junit reports
    }

    private void importWsdlFiles() {
        process.targetWsdlPaths.each {
            interfaceFactory.importWsdl project, it, false
        }
    }

    private void createTestCases() {
        soapUiTestSuite = project.addNewTestSuite(process.targetSoapUIProjectName)

        process.testCases.each { testCase ->
            createTestCase(testCase)
        }
    }

    private void createTestCase(TestCase testCase) {
        WsdlTestCase soapUITestCase = soapUiTestSuite.addNewTestCase(testCase.name)

        if (testCase.notDeployable) {
            addNotDeployableTestSteps(soapUITestCase)
            return
        }

        addDeployableTestSteps(soapUITestCase)

        testCase.testSteps.each { testStep ->
            int testStepNumber = testCase.testSteps.indexOf(testStep)
            WsdlTestRequestStep soapUiRequestStep = createTestStepConfig(soapUITestCase, testStep, testStepNumber)

            WsdlTestRequest soapUiRequest = createSoapUiRequest(soapUiRequestStep,testStep)

            if (!testStep.isOneWay()) {
                addSynchronousAssertion(testStep, soapUiRequest, soapUITestCase, soapUiRequestStep, testStepNumber)
            } else {
                addOneWayAssertion(soapUiRequest)
            }

            if (testStep.timeToWaitAfterwards != null) {
                addDelayTime(soapUITestCase, testStep, testStepNumber)
            }
        }
    }

    private WsdlTestRequest createSoapUiRequest(WsdlTestRequestStep soapUiRequestStep, TestStep testStep){
        WsdlTestRequest soapUiRequest = soapUiRequestStep.testRequest
        if (testStep.operation.equals(WsdlOperation.SYNC)) {
            soapUiRequest.requestContent = createSyncInputMessage(testStep.input)
        } else if (testStep.operation.equals(WsdlOperation.ASYNC)){
            soapUiRequest.requestContent = createAsyncInputMessage(testStep.input)
        }  else{
            soapUiRequest.requestContent = createSyncStringInputMessage(testStep.input)
        }
        soapUiRequest.timeout = requestTimeout
        return soapUiRequest
    }

    private void addSynchronousAssertion(TestStep testStep, WsdlTestRequest soapUiRequest, WsdlTestCase soapUITestCase, WsdlTestRequestStep soapUiRequestStep, int testStepNumber) {
        testStep.assertions.each {assertion ->

            if (assertion instanceof XpathTestAssertion) {
                addXpathTestAssertions(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof SoapFaultTestAssertion) {
                addSoapFaultTestAssertions(soapUiRequest, soapUiRequestStep, assertion)
            } else if (assertion instanceof ExitAssertion) {
                addExitAssertions(soapUiRequest, soapUiRequestStep, assertion, soapUITestCase, testStepNumber)
            }
        }

        if (!testStep.assertions.any {it instanceof SoapFaultTestAssertion || it instanceof ExitAssertion}) {
            soapUiRequest.addAssertion(NotSoapFaultAssertion.LABEL)
        }
    }

    private void addExitAssertions(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, ExitAssertion exitAssertion, WsdlTestCase soapUITestCase, int testStepNumber) {

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


    private void addXpathTestAssertions(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, XpathTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)

        XPathContainsAssertion xPathContainsAssertion = soapUiRequestStep.addAssertion(XPathContainsAssertion.LABEL) as XPathContainsAssertion
        xPathContainsAssertion.path = assertion.xpathExpression
        xPathContainsAssertion.expectedContent = assertion.expectedOutput
    }

    private void addSoapFaultTestAssertions(WsdlTestRequest soapUiRequest, WsdlTestRequestStep soapUiRequestStep, SoapFaultTestAssertion assertion) {
        // validate result
        soapUiRequest.addAssertion(SoapResponseAssertion.LABEL)
        soapUiRequest.addAssertion(SoapFaultAssertion.LABEL)

        if (assertion.faultString) {
            SimpleContainsAssertion simpleContainsAssertion = soapUiRequestStep.addAssertion(SimpleContainsAssertion.LABEL) as SimpleContainsAssertion
            simpleContainsAssertion.token = assertion.faultString
            simpleContainsAssertion.ignoreCase = false
        }
    }

    private void addNotDeployableTestSteps(WsdlTestCase soapUITestCase) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Unavailabilty of WSDL") as WsdlGroovyScriptTestStep
        groovyScriptTestStep.script = """
try {
 def url = new URL("${process.wsdlEndpoint}")
 def connection = url.openConnection()
 if(connection.responseCode == 500) {
    assert true, "500 error"
 } else {
    def text = connection.inputStream.text
    assert !text.contains("definitions>"), "wsdl file available - however, process should not be deployable"
 }
} catch (ConnectException e){
    assert true, "connection refused"
} catch (FileNotFoundException e){
    assert true, "file not found"
} catch (Exception e){
    testRunner.fail("error \${e.message}")
}
"""
    }

    private void addDeployableTestSteps(WsdlTestCase soapUITestCase) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Availabilty of WSDL") as WsdlGroovyScriptTestStep
        groovyScriptTestStep.script = """
try {
 def url = new URL("${process.wsdlEndpoint}")
 def connection = url.openConnection()
 connection.guessContentTypeFromName("test.xml")
 def text = connection.inputStream.text
 assert text.contains("definitions>"), "no wsdl file given - process is not deployed"
} catch (ConnectException e){
    assert false, "connection refused"
} catch (FileNotFoundException e){
    assert false, "file not found"
} catch (Exception e){
    assert false, "error \${e.message} \${e}"
}
"""
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

    private void saveProject() {
        project.saveAs(process.targetSoapUIFilePath)
    }


    private WsdlTestRequestStep createTestStepConfig(WsdlTestCase soapUiTestCase, TestStep testStep, int testStepNumber) {
        String iface = "TestInterfacePortTypeBinding"
        String operation = testStep.operation.name
        String wsdlOperationName = "Sending ${iface}.${operation} Step #${testStepNumber}"

        WsdlInterface inter_face = project.getInterfaceByName(iface) as WsdlInterface
        com.eviware.soapui.impl.wsdl.WsdlOperation op = inter_face.getOperationByName(operation)

        if (op == null) {
            throw new RuntimeException("WsdlOperation ${iface}.${operation} could not be found in soapUI project of process ${process} on engine ${process.engine}")
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

}

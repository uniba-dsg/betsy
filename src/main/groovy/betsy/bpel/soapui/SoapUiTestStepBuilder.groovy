package betsy.bpel.soapui

import betsy.common.model.steps.DelayTestStep
import betsy.common.model.steps.DeployableCheckTestStep
import betsy.common.model.steps.NotDeployableCheckTestStep
import betsy.common.model.steps.SoapTestStep
import betsy.common.model.TestCase
import betsy.common.model.TestStep
import betsy.common.model.steps.WsdlOperation
import com.eviware.soapui.config.TestStepConfig
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDelayTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.DelayStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import static SoapUiAssertionBuilder.*

class SoapUiTestStepBuilder {

    private TestCase testCase

    private WsdlTestCase soapUiTestCase

    private WsdlProject wsdlProject

    private int requestTimeout

    private String wsdlEndpoint

    public SoapUiTestStepBuilder(TestCase testCase, WsdlTestCase soapUiTestCase, WsdlProject wsdlProject, int requestTimeout, String wsdlEndpoint) {
        this.testCase = testCase
        this.soapUiTestCase = soapUiTestCase
        this.wsdlProject = wsdlProject
        this.requestTimeout = requestTimeout
        this.wsdlEndpoint = wsdlEndpoint
    }

    public void addTestStep(TestStep testStep) {
        int testStepNumber = testCase.testSteps.indexOf(testStep)

        if(testStep instanceof DeployableCheckTestStep) {
            addDeployableTestSteps(soapUiTestCase, wsdlEndpoint)
        } else if(testStep instanceof NotDeployableCheckTestStep) {
            addNotDeployableTestSteps(soapUiTestCase, wsdlEndpoint)
        } else if(testStep instanceof DelayTestStep) {
            addDelayTime(soapUiTestCase, (DelayTestStep) testStep, testStepNumber)
        } else if(testStep instanceof SoapTestStep) {
            if (testStep.isTestPartner()) {
                addStepForTestPartner(testStep, testStepNumber)
            } else {
                addStepForTestInterface(testStep, testStepNumber)
            }
        } else {
            throw new IllegalArgumentException("Given test step not recognized. Got " + testStep);
        }
    }

    public static void addDelayTime(WsdlTestCase soapUITestCase, DelayTestStep testStep, int testStepNumber) {
        WsdlDelayTestStep delay = soapUITestCase.addTestStep(DelayStepFactory.DELAY_TYPE, "Delay for Step #$testStepNumber") as WsdlDelayTestStep
        delay.setDelay(testStep.timeToWaitAfterwards)
    }

    public void addStepForTestPartner(SoapTestStep testStep, int testStepNumber) {
        WsdlTestRequestStep partnerRequestStep = createTestStepConfig(soapUiTestCase, testStepNumber, "TestPartnerPortTypeBinding","startProcessSync")
        createSoapUiRequest(partnerRequestStep, testStep)
        addTestPartnerAssertion(testStep, partnerRequestStep)
    }

    private WsdlTestRequestStep createTestStepConfig(WsdlTestCase soapUiTestCase, int testStepNumber, String portTypeName, String operationName) {
        String wsdlOperationName = "Sending ${portTypeName}.${operationName} Step #${testStepNumber}"

        WsdlInterface wsdlInterface = wsdlProject.getInterfaceByName(portTypeName) as WsdlInterface
        Objects.requireNonNull(wsdlInterface, "Cannot find WSDL Interface for PortType ${portTypeName}")

        com.eviware.soapui.impl.wsdl.WsdlOperation op = wsdlInterface.getOperationByName(operationName)
        Objects.requireNonNull(op, "WsdlOperation ${portTypeName}.${operationName} could not be found in soapUI project")

        TestStepConfig config = WsdlTestRequestStepFactory.createConfig(op, wsdlOperationName)
        Objects.requireNonNull(config, "Could not create config for ${wsdlOperationName}")

        WsdlTestStep soapUiTestStep = soapUiTestCase.addTestStep(config)
        Objects.requireNonNull(soapUiTestStep, "Could not create request step for ${wsdlOperationName}")

        soapUiTestStep as WsdlTestRequestStep
    }

    private WsdlTestRequest createSoapUiRequest(WsdlTestRequestStep soapUiRequestStep, SoapTestStep testStep) {
        WsdlTestRequest soapUiRequest = soapUiRequestStep.testRequest
        if (testStep.operation.equals(WsdlOperation.SYNC)) {
            soapUiRequest.requestContent = TestMessages.createSyncInputMessage(testStep.input)
        } else if (testStep.operation.equals(WsdlOperation.ASYNC)) {
            soapUiRequest.requestContent = TestMessages.createAsyncInputMessage(testStep.input)
        } else if (testStep.isTestPartner()) {
            soapUiRequest.requestContent = TestMessages.createSyncTestPartnerInputMessage(testStep.input)
        } else {
            soapUiRequest.requestContent = TestMessages.createSyncStringInputMessage(testStep.input)
        }
        soapUiRequest.timeout = requestTimeout
        return soapUiRequest
    }

    public void addStepForTestInterface(SoapTestStep testStep, int testStepNumber) {
        WsdlTestRequestStep soapUiRequestStep = createTestStepConfig(soapUiTestCase, testStepNumber, "TestInterfacePortTypeBinding", testStep.operation.name)
        WsdlTestRequest soapUiRequest = createSoapUiRequest(soapUiRequestStep, testStep)

        if (!testStep.isOneWay()) {
            addSynchronousAssertion(testStep, soapUiRequestStep, soapUiTestCase, testStepNumber)
        } else {
            addOneWayAssertion(soapUiRequest)
        }
    }

    private static void addNotDeployableTestSteps(WsdlTestCase soapUITestCase, String wsdlEndpoint) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Unavailabilty of WSDL") as WsdlGroovyScriptTestStep
        groovyScriptTestStep.script = """
try {
 def url = new URL("${wsdlEndpoint}")
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

    private static void addDeployableTestSteps(WsdlTestCase soapUITestCase, String wsdlEndpoint) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Availabilty of WSDL") as WsdlGroovyScriptTestStep
        groovyScriptTestStep.script = """
try {
 def url = new URL("${wsdlEndpoint}")
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

}

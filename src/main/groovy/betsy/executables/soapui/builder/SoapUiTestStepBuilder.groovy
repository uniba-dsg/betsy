package betsy.executables.soapui.builder

import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.WsdlOperation
import com.eviware.soapui.config.TestStepConfig
import com.eviware.soapui.impl.wsdl.WsdlInterface
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory
import static SoapUIAssertionBuilder.*

class SoapUiTestStepBuilder {

    private TestCase testCase

    private WsdlTestCase soapUiTestCase

    private WsdlProject wsdlProject

    private int requestTimeout

    public SoapUiTestStepBuilder(TestCase testCase, WsdlTestCase soapUiTestCase, WsdlProject wsdlProject, int requestTimeout) {
        this.testCase = testCase
        this.soapUiTestCase = soapUiTestCase
        this.wsdlProject = wsdlProject
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
		if(wsdlInterface == null){
			throw new RuntimeException("Cannot find WSDL Interface for PortType ${portTypeName}")
		}
        com.eviware.soapui.impl.wsdl.WsdlOperation op = wsdlInterface.getOperationByName(operationName)

        if (op == null) {
            throw new RuntimeException("WsdlOperation ${portTypeName}.${operationName} could not be found in soapUI project")
        }

        TestStepConfig config = WsdlTestRequestStepFactory.createConfig(op, wsdlOperationName)

        if (config == null) {
            throw new RuntimeException("Could not create config for ${wsdlOperationName}")
        }

        WsdlTestStep soapUiTestStep = soapUiTestCase.addTestStep(config)

        if (soapUiTestStep == null) {
            throw new RuntimeException("Could not create request step for ${wsdlOperationName}")
        }

        soapUiTestStep as WsdlTestRequestStep
    }

    private WsdlTestRequest createSoapUiRequest(WsdlTestRequestStep soapUiRequestStep, TestStep testStep) {
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

}

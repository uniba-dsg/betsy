package betsy.bpel.soapui;

import betsy.bpel.model.steps.DelayTestStep;
import betsy.bpel.model.steps.DeployableCheckTestStep;
import betsy.bpel.model.steps.NotDeployableCheckTestStep;
import betsy.bpel.model.steps.SoapTestStep;
import betsy.common.model.TestCase;
import betsy.common.model.TestStep;
import com.eviware.soapui.config.TestStepConfig;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.*;
import com.eviware.soapui.impl.wsdl.teststeps.registry.DelayStepFactory;
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory;
import com.eviware.soapui.impl.wsdl.teststeps.registry.WsdlTestRequestStepFactory;

import java.util.Objects;

public class SoapUiTestStepBuilder {

    private final TestCase testCase;
    private final WsdlTestCase soapUiTestCase;
    private final WsdlProject wsdlProject;
    private final int requestTimeout;
    private final String wsdlEndpoint;

    public SoapUiTestStepBuilder(TestCase testCase, WsdlTestCase soapUiTestCase, WsdlProject wsdlProject, int requestTimeout, String wsdlEndpoint) {
        this.testCase = testCase;
        this.soapUiTestCase = soapUiTestCase;
        this.wsdlProject = wsdlProject;
        this.requestTimeout = requestTimeout;
        this.wsdlEndpoint = wsdlEndpoint;
    }

    public void addTestStep(TestStep testStep) {
        int testStepNumber = testCase.getTestSteps().indexOf(testStep);

        if (testStep instanceof DeployableCheckTestStep) {
            addDeployableTestSteps(soapUiTestCase, wsdlEndpoint);
        } else if (testStep instanceof NotDeployableCheckTestStep) {
            addNotDeployableTestSteps(soapUiTestCase, wsdlEndpoint);
        } else if (testStep instanceof DelayTestStep) {
            addDelayTime(soapUiTestCase, (DelayTestStep) testStep, testStepNumber);
        } else if (testStep instanceof SoapTestStep) {
            if (((SoapTestStep) testStep).isTestPartner()) {
                addStepForTestPartner((SoapTestStep) testStep, testStepNumber);
            } else {
                addStepForTestInterface((SoapTestStep) testStep, testStepNumber);
            }

        } else {
            throw new IllegalArgumentException("Given test step not recognized. Got " + testStep);
        }

    }

    public static void addDelayTime(WsdlTestCase soapUITestCase, DelayTestStep testStep, int testStepNumber) {
        WsdlDelayTestStep delay = (WsdlDelayTestStep) soapUITestCase.addTestStep(DelayStepFactory.DELAY_TYPE, "Delay for Step #" + String.valueOf(testStepNumber));
        delay.setDelay(testStep.getTimeToWaitAfterwards());
    }

    public void addStepForTestPartner(SoapTestStep testStep, int testStepNumber) {
        WsdlTestRequestStep partnerRequestStep = createTestStepConfig(soapUiTestCase, testStepNumber, "TestPartnerPortTypeBinding", "startProcessSync");
        createSoapUiRequest(partnerRequestStep, testStep);
        SoapUiAssertionBuilder.addTestPartnerAssertion(testStep, partnerRequestStep);
    }

    private WsdlTestRequestStep createTestStepConfig(WsdlTestCase soapUiTestCase, final int testStepNumber, final String portTypeName, final String operationName) {
        final String wsdlOperationName = "Sending " + portTypeName + "." + operationName + " Step #" + String.valueOf(testStepNumber);

        WsdlInterface wsdlInterface = (WsdlInterface) wsdlProject.getInterfaceByName(portTypeName);
        Objects.requireNonNull(wsdlInterface, "Cannot find WSDL Interface for PortType " + portTypeName);

        WsdlOperation op = wsdlInterface.getOperationByName(operationName);
        Objects.requireNonNull(op, "WsdlOperation " + portTypeName + "." + operationName + " could not be found in soapUI project");

        TestStepConfig config = WsdlTestRequestStepFactory.createConfig(op, wsdlOperationName);
        Objects.requireNonNull(config, "Could not create config for " + wsdlOperationName);

        WsdlTestStep soapUiTestStep = soapUiTestCase.addTestStep(config);
        Objects.requireNonNull(soapUiTestStep, "Could not create request step for " + wsdlOperationName);

        return (WsdlTestRequestStep) soapUiTestStep;
    }

    private WsdlTestRequest createSoapUiRequest(WsdlTestRequestStep soapUiRequestStep, SoapTestStep testStep) {
        WsdlTestRequest soapUiRequest = soapUiRequestStep.getTestRequest();
        if (betsy.bpel.model.steps.WsdlOperation.SYNC.equals(testStep.getOperation())) {
            soapUiRequest.setRequestContent(TestMessages.createSyncInputMessage(testStep.getInput()));
        } else if (betsy.bpel.model.steps.WsdlOperation.ASYNC.equals(testStep.getOperation())) {
            soapUiRequest.setRequestContent(TestMessages.createAsyncInputMessage(testStep.getInput()));
        } else if (testStep.isTestPartner()) {
            soapUiRequest.setRequestContent(TestMessages.createSyncTestPartnerInputMessage(testStep.getInput()));
        } else {
            soapUiRequest.setRequestContent(TestMessages.createSyncStringInputMessage(testStep.getInput()));
        }

        soapUiRequest.setTimeout(String.valueOf(requestTimeout));
        return soapUiRequest;
    }

    public void addStepForTestInterface(SoapTestStep testStep, int testStepNumber) {
        WsdlTestRequestStep soapUiRequestStep = createTestStepConfig(soapUiTestCase, testStepNumber, "TestInterfacePortTypeBinding", testStep.getOperation().getName());
        WsdlTestRequest soapUiRequest = createSoapUiRequest(soapUiRequestStep, testStep);

        if (!testStep.isOneWay()) {
            SoapUiAssertionBuilder.addSynchronousAssertion(testStep, soapUiRequestStep, soapUiTestCase, testStepNumber);
        } else {
            SoapUiAssertionBuilder.addOneWayAssertion(soapUiRequest);
        }

    }

    private static void addNotDeployableTestSteps(WsdlTestCase soapUITestCase, final String wsdlEndpoint) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = (WsdlGroovyScriptTestStep) soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Unavailability of WSDL");
        groovyScriptTestStep.setScript("\n" +
                "try {\n" +
                " def url = new URL(\"" + wsdlEndpoint + "\")\n" +
                " def connection = url.openConnection()\n" +
                " if(connection.responseCode == 500) {\n" +
                "    assert true, \"500 error\"\n" +
                " } else {\n" +
                "    def text = connection.inputStream.text\n" +
                "    assert !text.contains(\"definitions>\"), \"wsdl file available - however, process should not be deployable\"\n" +
                " }\n" +
                "} catch (ConnectException e){\n" +
                "    assert true, \"connection refused\"\n" +
                "} catch (FileNotFoundException e){\n" +
                "    assert true, \"file not found\"\n" +
                "} catch (Exception e){\n" +
                "    testRunner.fail(\"error ${e.message}\")\n" +
                "}\n");
    }

    private static void addDeployableTestSteps(WsdlTestCase soapUITestCase, final String wsdlEndpoint) {
        WsdlGroovyScriptTestStep groovyScriptTestStep = (WsdlGroovyScriptTestStep) soapUITestCase.addTestStep(GroovyScriptStepFactory.GROOVY_TYPE, "Test Availability of WSDL");
        groovyScriptTestStep.setScript("\n" +
                "try {\n" +
                " def url = new URL(\"" + wsdlEndpoint + "\")\n" +
                " def connection = url.openConnection()\n" +
                " connection.guessContentTypeFromName(\"test.xml\")\n" +
                " def text = connection.inputStream.text\n" +
                " assert text.contains(\"definitions>\"), \"no wsdl file given - process is not deployed\"\n" +
                "} catch (ConnectException e){\n" +
                "    assert false, \"connection refused\"\n" +
                "} catch (FileNotFoundException e){\n" +
                "    assert false, \"file not found\"\n" +
                "} catch (Exception e){\n" +
                "    assert false, \"error ${e.message} ${e}\"\n" +
                "}\n");
    }


}

package betsy.bpel.soapui;

import pebl.test.TestCase;
import pebl.test.TestStep;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;

public class SoapUiTestCaseBuilder {
    private final WsdlTestSuite soapUiTestSuite;
    private final WsdlProject wsdlProject;
    private final String wsdlEndpoint;
    private final int requestTimeout;

    public SoapUiTestCaseBuilder(WsdlTestSuite soapUiTestSuite, WsdlProject project, String wsdlEndpoint, int requestTimeout) {
        this.soapUiTestSuite = soapUiTestSuite;
        this.wsdlProject = project;
        this.wsdlEndpoint = wsdlEndpoint;
        this.requestTimeout = requestTimeout;
    }

    public void addTestCase(TestCase testCase) {
        WsdlTestCase soapUITestCase = soapUiTestSuite.addNewTestCase(testCase.getName());

        SoapUiTestStepBuilder testStepBuilder = new SoapUiTestStepBuilder(testCase, soapUITestCase, wsdlProject, requestTimeout, wsdlEndpoint);
        for (TestStep testStep : testCase.getTestSteps()) {
            testStepBuilder.addTestStep(testStep);
        }
    }
}

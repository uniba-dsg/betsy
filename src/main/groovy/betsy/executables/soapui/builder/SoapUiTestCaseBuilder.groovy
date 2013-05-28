package betsy.executables.soapui.builder

import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import betsy.data.TestCase
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.WsdlProject


class SoapUiTestCaseBuilder {

    private WsdlTestSuite soapUiTestSuite

    private WsdlProject wsdlProject

    private String wsdlEndpoint

    private int requestTimeout

    public SoapUiTestCaseBuilder(WsdlTestSuite soapUiTestSuite, WsdlProject project, String wsdlEndpoint, int requestTimeout) {
        this.soapUiTestSuite = soapUiTestSuite
        this.wsdlProject = project
        this.wsdlEndpoint = wsdlEndpoint
        this.requestTimeout = requestTimeout
    }

    public void addTestCase(TestCase testCase) {
        WsdlTestCase soapUITestCase = soapUiTestSuite.addNewTestCase(testCase.name)

        SoapUiTestStepBuilder testStepBuilder = new SoapUiTestStepBuilder(testCase, soapUITestCase, wsdlProject, requestTimeout, wsdlEndpoint)
        testCase.testSteps.each { testStep ->
           testStepBuilder.addTestStep(testStep)
        }

    }



}

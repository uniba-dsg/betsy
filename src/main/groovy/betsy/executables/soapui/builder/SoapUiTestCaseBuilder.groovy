package betsy.executables.soapui.builder

import com.eviware.soapui.impl.wsdl.WsdlTestSuite
import betsy.data.TestCase
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase
import com.eviware.soapui.impl.wsdl.teststeps.WsdlGroovyScriptTestStep
import com.eviware.soapui.impl.wsdl.teststeps.registry.GroovyScriptStepFactory
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

        addDeploymentTestSteps(soapUITestCase, testCase)

        SoapUiTestStepBuilder testStepBuilder = new SoapUiTestStepBuilder(testCase, soapUITestCase, wsdlProject, requestTimeout)
        testCase.testSteps.each { testStep ->
           testStepBuilder.addTestStep(testStep)
        }

    }

    private void addDeploymentTestSteps(WsdlTestCase soapUiTestCase, TestCase testCase) {
        if (testCase.notDeployable) {
            addNotDeployableTestSteps(soapUiTestCase)
        } else {
            addDeployableTestSteps(soapUiTestCase)
        }
    }

    private void addNotDeployableTestSteps(WsdlTestCase soapUITestCase) {
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

    private void addDeployableTestSteps(WsdlTestCase soapUITestCase) {
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

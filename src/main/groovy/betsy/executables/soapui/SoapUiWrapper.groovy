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

        SoapUiTestCaseBuilder testCaseBuilder = new SoapUiTestCaseBuilder(soapUiTestSuite,project,process,requestTimeout)
        process.testCases.each { testCase ->
            testCaseBuilder.addTestCase(testCase)
        }
    }

    private void saveProject() {
        project.saveAs(process.targetSoapUIFilePath)
    }

}

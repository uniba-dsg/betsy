package betsy.executables.soapui.builder

import betsy.data.Process
import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlProjectFactory
import com.eviware.soapui.impl.wsdl.WsdlTestSuite


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
        WsdlTestSuite soapUiTestSuite = project.addNewTestSuite(process.targetSoapUIProjectName)

        SoapUiTestCaseBuilder testCaseBuilder = new SoapUiTestCaseBuilder(soapUiTestSuite,project,process.wsdlEndpoint,requestTimeout)
        process.testCases.each { testCase ->
            testCaseBuilder.addTestCase(testCase)
        }
    }

    private void saveProject() {
        project.saveAs(process.targetSoapUIFilePath)
    }

}

package betsy.bpel.soapui

import betsy.bpel.model.BetsyProcess
import betsy.common.model.TestCase
import com.eviware.soapui.impl.WsdlInterfaceFactory
import com.eviware.soapui.impl.wsdl.WsdlProject
import com.eviware.soapui.impl.wsdl.WsdlProjectFactory
import com.eviware.soapui.impl.wsdl.WsdlTestSuite


/**
 * Creates a soapUI project from a Process.
 *
 * Not Thread-Safe!
 */
class SoapUiProjectBuilder {

    private final WsdlProjectFactory projectFactory = new WsdlProjectFactory()
    private final WsdlInterfaceFactory interfaceFactory = new WsdlInterfaceFactory()

    BetsyProcess process
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
        for(String it : process.targetWsdlPaths) {
            if(!it.contains("TestIF")) {
                interfaceFactory.importWsdl project, it, false
            }
        }
    }

    private void createTestCases() {
        WsdlTestSuite soapUiTestSuite = project.addNewTestSuite(process.targetSoapUIProjectName)

        SoapUiTestCaseBuilder testCaseBuilder = new SoapUiTestCaseBuilder(soapUiTestSuite,project,process.wsdlEndpoint,requestTimeout)
        for(TestCase testCase : process.testCases) {
            testCaseBuilder.addTestCase(testCase)
        }
    }

    private void saveProject() {
        project.saveAs(process.targetSoapUIFilePath.toString())
    }

}

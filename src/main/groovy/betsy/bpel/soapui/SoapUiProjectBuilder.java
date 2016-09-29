package betsy.bpel.soapui;

import betsy.bpel.model.BPELProcess;
import pebl.benchmark.test.TestCase;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlProjectFactory;
import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.support.SoapUIException;
import org.apache.xmlbeans.XmlException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Creates a soapUI project from a Process.
 * <p>
 * Not Thread-Safe!
 */
public class SoapUiProjectBuilder {

    private final WsdlProjectFactory projectFactory = new WsdlProjectFactory();
    private BPELProcess process;
    private int requestTimeout;
    private WsdlProject project;

    public void createSoapUIProject() {
        try {
            createProject();
        } catch (Exception e) {
            throw new RuntimeException("could not create soapui project", e);
        }
        try {
            importWsdlFiles();
        } catch (Exception e) {
            throw new RuntimeException("could not import wsdls", e);
        }
        createTestCases();
        saveProject();
    }

    private void createProject() throws XmlException, IOException, SoapUIException {
        project = projectFactory.createNew();
        project.setName("soapui");// this is also the top level package for junit reports
    }

    private void importWsdlFiles() throws SoapUIException {
        for (Path it : process.getTargetWsdlPaths()) {
            if (!it.toString().contains("TestIF")) {
                WsdlInterfaceFactory.importWsdl(project, it.toString(), false);
            }
        }
    }

    private void createTestCases() {
        WsdlTestSuite soapUiTestSuite = project.addNewTestSuite(process.getTargetSoapUIProjectName());

        SoapUiTestCaseBuilder testCaseBuilder = new SoapUiTestCaseBuilder(soapUiTestSuite, project, process.getWsdlEndpoint(), requestTimeout);
        for (TestCase testCase : process.getTestCases()) {
            testCaseBuilder.addTestCase(testCase);
        }

    }

    private void saveProject() {
        Path targetSoapUIFilePath = process.getTargetSoapUIFilePath();
        try {
            project.saveAs(targetSoapUIFilePath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Could not save soapUI project at " + targetSoapUIFilePath, e);
        }
    }

    public BPELProcess getProcess() {
        return process;
    }

    public void setProcess(BPELProcess process) {
        this.process = process;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

}

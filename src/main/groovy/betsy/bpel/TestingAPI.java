package betsy.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.soapui.TestBuilder;
import betsy.bpel.ws.DummyAndRegularTestPartnerService;
import betsy.bpel.ws.TestPartnerService;
import soapui.SoapUiRunner;
import timeouts.timeout.TimeoutRepository;

import java.nio.file.Path;

public class TestingAPI implements TestPartnerService{

    private TestPartnerService testPartner = new DummyAndRegularTestPartnerService();
    private int requestTimeout = TimeoutRepository.getTimeout("TestingAPI.constructor").get().getTimeoutInMs();

    @Override
    public void startup() {
        testPartner.startup();
    }

    @Override
    public void shutdown() {
        testPartner.shutdown();
    }

    @Override
    public boolean isRunning() {
        return testPartner.isRunning();
    }

    public void executeEngineDependentTest(Path soapUIFilePath, Path targetReportsPath) {
        new SoapUiRunner(soapUIFilePath, targetReportsPath).run();
    }

    public void generateEngineDependentTest(BPELProcess process) {
        new TestBuilder(process, requestTimeout).buildTest();
    }

    public void setTestPartner(TestPartnerService testPartner) {
        this.testPartner = testPartner;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }


}

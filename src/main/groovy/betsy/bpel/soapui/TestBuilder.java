package betsy.bpel.soapui;

import betsy.bpel.model.BPELProcess;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

public class TestBuilder {
    private static final Logger log = Logger.getLogger(TestBuilder.class);

    private final BPELProcess process;

    /**
     * timeout for pending response in milliseconds
     */
    private final int requestTimeout;

    public TestBuilder(BPELProcess process, int requestTimeout) {
        this.process = process;
        this.requestTimeout = requestTimeout;
    }

    public void buildTest() {
        log.info("Creating SoapUI TestSuite");
        FileTasks.mkdirs(process.getTargetSoapUIPath());
        FileTasks.mkdirs(process.getTargetReportsPath());

        SoapUiProjectBuilder builder = new SoapUiProjectBuilder();
        builder.setProcess(process);
        builder.setRequestTimeout(requestTimeout);
        builder.createSoapUIProject();
    }
}

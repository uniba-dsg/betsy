package betsy.executables.soapui.builder

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks
import org.apache.log4j.Logger


class TestBuilder {

    private static final Logger log = Logger.getLogger(TestBuilder.class)

    final AntBuilder ant = AntUtil.builder()

    BetsyProcess process

    /**
     * timeout for pending response in milliseconds
     */
    int requestTimeout

    public void buildTest() {
        log.info "Creating SoapUI TestSuite"
        FileTasks.mkdirs(process.targetSoapUIPath)
        new SoapUiProjectBuilder(process: process, requestTimeout: requestTimeout).createSoapUIProject()
    }
}

package betsy.bpel.engines.wso2;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.time.LocalDate;

public class Wso2Engine_v2_1_2 extends Wso2Engine_v3_1_0 {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "wso2", "2.1.2", LocalDate.of(2011, 10, 30), "Apache-2.0");
    }

    @Override
    public Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-2.1.2");
    }

    @Override
    public void install() {
        super.install();

        Path windowsStartupScript = getBinDir().resolve("wso2server.bat");
        FileTasks.deleteLine(windowsStartupScript, 150);
        FileTasks.deleteLine(windowsStartupScript, 150);


        Path unixStartupScript = getBinDir().resolve("wso2server.sh");
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);
        FileTasks.deleteLine(unixStartupScript, 217);

    }

    @Override
    public String getZipFileName() {
        return "wso2bps-2.1.2.zip";
    }

}

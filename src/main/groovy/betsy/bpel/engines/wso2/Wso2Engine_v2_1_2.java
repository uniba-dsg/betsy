package betsy.bpel.engines.wso2;

import java.nio.file.Path;
import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.FileTasks;

public class Wso2Engine_v2_1_2 extends Wso2Engine_v3_1_0 {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "wso2", "2.1.2", LocalDate.of(2011, 10, 30), "Apache-2.0");
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

        Path file32bitWrapper = getBinDir().resolve("native").resolve("wrapper-linux-x86-32");
        FileTasks.move(file32bitWrapper, file32bitWrapper.getParent().resolve("wrapper-linux-x86-32.bak"));
    }

    @Override
    public String getZipFileName() {
        return "wso2bps-2.1.2.zip";
    }

}

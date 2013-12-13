package betsy.data.engines.wso2

import betsy.tasks.FileTasks

import java.nio.file.Path

class Wso2Engine_v2_1_2 extends Wso2Engine_v3_1_0 {

    @Override
    String getName() {
        return "wso2_v2_1_2"
    }

    @Override
    Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-2.1.2")
    }

    @Override
    void install() {
        super.install()

        Path startupScript = getBinDir().resolve("wso2server.bat")
        FileTasks.deleteLine(startupScript, 150);
        FileTasks.deleteLine(startupScript, 150);
    }

    @Override
    String getZipFileName() {
        return "wso2bps-2.1.2.zip"
    }

}

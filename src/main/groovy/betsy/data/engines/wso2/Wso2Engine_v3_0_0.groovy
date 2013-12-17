package betsy.data.engines.wso2

import java.nio.file.Path

class Wso2Engine_v3_0_0 extends Wso2Engine_v3_1_0 {

    @Override
    String getName() {
        return "wso2_v3_0_0"
    }

    @Override
    Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-3.0.0")
    }

    @Override
    String getZipFileName() {
        return "wso2bps-3.0.0.zip"
    }

}

package betsy.bpel.engines.wso2;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.Engine;

import java.nio.file.Path;

public class Wso2Engine_v3_0_0 extends Wso2Engine_v3_1_0 {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "wso2", "3.0.0");
    }

    @Override
    public Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-3.0.0");
    }

    @Override
    public String getZipFileName() {
        return "wso2bps-3.0.0.zip";
    }

}

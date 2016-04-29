package betsy.bpel.engines.wso2;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;

import java.nio.file.Path;
import java.time.LocalDate;

public class Wso2Engine_v3_0_0 extends Wso2Engine_v3_1_0 {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "wso2", "3.0.0", LocalDate.of(2012, 10, 17));
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

package betsy.bpel.engines.openesb;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;

public class OpenEsb305StandaloneEngine extends OpenEsb301StandaloneEngine{

    public OpenEsb305StandaloneEngine(){
        super("OpenESB-SE-3.0.5", "OpenESB-SE-3.0.5-server-only.zip","openesb-oeadmin-1.0.2.jar");
    }

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "openesb", "3.0.5");
    }

}

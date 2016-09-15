package betsy.bpel.engines.openesb;

import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;

public class OpenEsb305StandaloneEngine extends OpenEsb301StandaloneEngine {

    public OpenEsb305StandaloneEngine() {
        super("OpenESB-SE-3.0.5", "OpenESB-SE-3.0.5-server-only.zip", "openesb-oeadmin-1.0.2.jar");
    }

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "openesb", "3.0.5", LocalDate.of(2015, 6, 24), "CDDL-1.0");
    }

}

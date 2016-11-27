package betsy.bpel.engines.ode;

import java.nio.file.Path;
import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import betsy.common.util.ClasspathHelper;
import pebl.ProcessLanguage;

public class OdeInMemoryEngine extends OdeEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "ode", "1.3.5", "in-memory", LocalDate.of(2011,2,6), "Apache-2.0");
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

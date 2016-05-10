package betsy.bpel.engines.ode;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.time.LocalDate;

public class OdeInMemoryEngine extends OdeEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "ode", "1.3.5", "in-memory", LocalDate.of(2011,2,6), "Apache-2.0");
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

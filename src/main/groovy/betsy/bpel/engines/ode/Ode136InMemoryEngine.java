package betsy.bpel.engines.ode;

import java.nio.file.Path;
import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import betsy.common.util.ClasspathHelper;
import pebl.ProcessLanguage;

public class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "ode", "1.3.6", "in-memory", LocalDate.of(2013,10,12), "Apache-2.0");
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

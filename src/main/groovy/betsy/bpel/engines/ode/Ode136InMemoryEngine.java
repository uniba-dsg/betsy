package betsy.bpel.engines.ode;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.time.LocalDate;

public class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "ode", "1.3.6", "in-memory", LocalDate.of(2013,10,12), "Apache-2.0");
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

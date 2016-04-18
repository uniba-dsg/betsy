package betsy.bpel.engines.ode;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class OdeInMemoryEngine extends OdeEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "ode", "1.3.5", "in-memory");
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

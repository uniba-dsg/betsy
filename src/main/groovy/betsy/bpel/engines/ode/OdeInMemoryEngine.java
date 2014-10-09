package betsy.bpel.engines.ode;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class OdeInMemoryEngine extends OdeEngine {
    @Override
    public String getName() {
        return "ode-in-memory";
    }

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

package betsy.bpel.engines.ode;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Ode136InMemoryEngine extends Ode136Engine {
    @Override
    public String getName() {
        return "ode136-in-memory";
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory");
    }

}

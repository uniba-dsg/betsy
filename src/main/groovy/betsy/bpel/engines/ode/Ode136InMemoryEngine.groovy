package betsy.bpel.engines.ode

import betsy.common.util.ClasspathHelper

import java.nio.file.Path
import java.nio.file.Paths

class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    String getName() {
        "ode136-in-memory"
    }

    Path getXsltPath() {
        ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory")
    }

}

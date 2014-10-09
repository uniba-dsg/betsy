package betsy.bpel.engines.ode

import betsy.common.util.ClasspathHelper

import java.nio.file.Path
import java.nio.file.Paths

class OdeInMemoryEngine extends OdeEngine {

    @Override
    String getName() {
        "ode-in-memory"
    }

    @Override
    Path getXsltPath() {
        ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode-in-memory")
    }

}
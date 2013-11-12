package betsy.data.engines.ode

import java.nio.file.Path
import java.nio.file.Paths

class OdeInMemoryEngine extends OdeEngine {

    @Override
    String getName() {
        "ode-in-memory"
    }

    @Override
    Path getXsltPath() {
        Paths.get("src/main/xslt/ode-in-memory")
    }

}
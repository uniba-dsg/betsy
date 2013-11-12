package betsy.data.engines.ode

import java.nio.file.Path
import java.nio.file.Paths

class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    String getName() {
        "ode136-in-memory"
    }

    Path getXsltPath() {
        Paths.get("src/main/xslt/ode-in-memory")
    }

}

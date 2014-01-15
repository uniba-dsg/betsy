package betsy.data.engines.ode

import java.nio.file.Path
import java.nio.file.Paths

class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    String getName() {
        "ode136-in-memory"
    }

    Path getXsltPath() {
        Paths.get(Ode136InMemoryEngine.class.getResource("/ode-in-memory").toURI())
    }

}

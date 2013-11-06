package betsy.data.engines.ode

class Ode136InMemoryEngine extends Ode136Engine {

    @Override
    String getName() {
        "ode136-in-memory"
    }

    String getXsltPath() {
        "src/main/xslt/ode-in-memory"
    }

}

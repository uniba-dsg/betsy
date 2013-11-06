package betsy.data.engines.ode

class OdeInMemoryEngine extends OdeEngine {

    @Override
    String getName() {
        "ode-in-memory"
    }

    @Override
    String getXsltPath() {
        "src/main/xslt/ode-in-memory"
    }

}
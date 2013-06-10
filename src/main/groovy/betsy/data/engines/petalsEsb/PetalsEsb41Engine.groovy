package betsy.data.engines.petalsEsb

class PetalsEsb41Engine extends PetalsEsbEngine {

    @Override
    String getName() {
        "petalsesb41"
    }

    String getFolder() {
        "petals-esb-4.1"
    }

    String getXsltPath() {
        "src/main/xslt/${super.getName()}"
    }

}

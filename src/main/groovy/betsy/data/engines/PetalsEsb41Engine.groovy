package betsy.data.engines

import betsy.data.Engine
import betsy.data.Process
import betsy.data.engines.packager.PetalsEsbCompositePackager

import java.nio.file.Files
import java.nio.file.Paths

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

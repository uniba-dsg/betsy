package betsy.data.engines.petalsesb

import java.nio.file.Path
import java.nio.file.Paths

class PetalsEsb41Engine extends PetalsEsbEngine {

    @Override
    String getName() {
        "petalsesb41"
    }

    String getFolder() {
        "petals-esb-4.1"
    }

    Path getXsltPath() {
        Paths.get("src/main/xslt/petalsesb")
    }

    @Override
    void install() {
        new PetalsEsbInstaller(serverDir: "server/petalsesb41",
                fileName: "petals-esb-distrib-4.1.0.zip",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/petals-esb-distrib-4.1.0.zip",
                targetEsbInstallDir: "server/petalsesb41/petals-esb-4.1/install",
                bpelComponentPath: "server/petalsesb41/petals-esb-distrib-4.1.0/esb-components/petals-se-bpel-1.1.0.zip",
                soapComponentPath: "server/petalsesb41/petals-esb-distrib-4.1.0/esb-components/petals-bc-soap-4.2.0.zip",
                sourceFile: "server/petalsesb41/petals-esb-distrib-4.1.0/esb/petals-esb-4.1.zip"
        ).install()
    }
}

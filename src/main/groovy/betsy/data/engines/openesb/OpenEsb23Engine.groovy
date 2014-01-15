package betsy.data.engines.openesb

import java.nio.file.Path
import java.nio.file.Paths

class OpenEsb23Engine extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb23"
    }

    Path getXsltPath() {
        Paths.get(OpenEsb23Engine.class.getResource("/openesb").toURI())
    }

    @Override
    void install() {
        new OpenEsbInstaller(fileName: "openesb-v23-installer-windows.exe",
                serverDir: Paths.get("server/openesb23"),
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/openesb-v23-installer-windows.exe",
                stateXmlTemplate: Paths.get(OpenEsbInstaller.class.getResource("/openesb23/state.xml.template").toURI())
        ).install()
    }

}

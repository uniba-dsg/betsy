package betsy.data.engines.openesb

import java.nio.file.Path
import java.nio.file.Paths

class OpenEsb23Engine extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb23"
    }

    Path getXsltPath() {
        Paths.get("src/main/xslt/openesb")
    }

    @Override
    void install() {
        new OpenEsbInstaller(fileName: "openesb-v23-installer-windows.exe",
                serverDir: "server/openesb23",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/openesb-v23-installer-windows.exe",
                stateXmlTemplate: "src/main/resources/openesb23/state.xml.template"
        ).install()
    }
}

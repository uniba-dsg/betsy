package betsy.data.engines.openesb

import java.nio.file.Path
import java.nio.file.Paths


class OpenEsb231Engine  extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb231"
    }

    Path getXsltPath() {
        Paths.get("src/main/xslt/openesb")
    }

    @Override
    void install() {
        new OpenEsbInstaller(fileName: "openesb-v231-installer-windows.exe",
                serverDir: Paths.get("server/openesb231"),
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/openesb-v231-installer-windows.exe",
                stateXmlTemplate: "src/main/resources/openesb231/state.xml.template"
        ).install()
    }

}
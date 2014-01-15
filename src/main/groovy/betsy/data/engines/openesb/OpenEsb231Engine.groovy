package betsy.data.engines.openesb

import java.nio.file.Path
import java.nio.file.Paths


class OpenEsb231Engine  extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb231"
    }

    Path getXsltPath() {
        Paths.get(OpenEsb231Engine.class.getResource("/openesb").toURI())
    }

    @Override
    void install() {
        new OpenEsbInstaller(fileName: "openesb-v231-installer-windows.exe",
                serverDir: Paths.get("server/openesb231"),
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/openesb-v231-installer-windows.exe",
                stateXmlTemplate: Paths.get(OpenEsbInstaller.class.getResource("/openesb231/state.xml.template").toURI())
        ).install()
    }

}
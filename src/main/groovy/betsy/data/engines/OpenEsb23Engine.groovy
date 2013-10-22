package betsy.data.engines

import betsy.data.engines.installer.OpenEsbInstaller

class OpenEsb23Engine extends OpenEsbEngine {

    @Override
    String getName() {
        "openesb23"
    }

    String getXsltPath() {
        "src/main/xslt/${super.getName()}"
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

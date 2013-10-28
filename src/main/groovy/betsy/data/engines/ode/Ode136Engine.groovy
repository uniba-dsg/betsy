package betsy.data.engines.ode

import betsy.data.engines.installer.OdeInstaller

class Ode136Engine extends OdeEngine {

    @Override
    String getName() {
        "ode136"
    }

    String getXsltPath() {
        "src/main/xslt/${super.getName()}"
    }

    @Override
    void install() {
        new OdeInstaller(fileName: "apache-ode-war-1.3.6.zip",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-ode-war-1.3.6.zip",
                odeName: "apache-ode-war-1.3.6",
                serverDir: getServerPath()).install()
    }
}

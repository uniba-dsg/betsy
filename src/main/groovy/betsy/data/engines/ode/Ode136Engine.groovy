package betsy.data.engines.ode

class Ode136Engine extends OdeEngine {

    @Override
    String getName() {
        "ode136"
    }

    String getXsltPath() {
        "src/main/xslt/ode"
    }

    @Override
    void install() {
        new OdeInstaller(fileName: "apache-ode-war-1.3.6.zip",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-ode-war-1.3.6.zip",
                odeName: "apache-ode-war-1.3.6",
                serverDir: getServerPath()).install()
    }
}

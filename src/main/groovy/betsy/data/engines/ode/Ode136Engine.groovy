package betsy.data.engines.ode

import java.nio.file.Path
import java.nio.file.Paths

class Ode136Engine extends OdeEngine {

    @Override
    String getName() {
        "ode136"
    }

    Path getXsltPath() {
        Paths.get(Ode136Engine.class.getResource("/ode").toURI())
    }

    @Override
    void install() {
        new OdeInstaller(fileName: "apache-ode-war-1.3.6.zip",
                downloadUrl: "https://lspi.wiai.uni-bamberg.de/svn/betsy/apache-ode-war-1.3.6.zip",
                odeName: "apache-ode-war-1.3.6",
                serverDir: getServerPath()).install()
    }
}

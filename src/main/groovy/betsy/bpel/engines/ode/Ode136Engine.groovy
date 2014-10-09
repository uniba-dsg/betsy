package betsy.bpel.engines.ode

import betsy.common.util.ClasspathHelper

import java.nio.file.Path
import java.nio.file.Paths

class Ode136Engine extends OdeEngine {

    @Override
    String getName() {
        "ode136"
    }

    Path getXsltPath() {
        ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode")
    }

    @Override
    void install() {
        new OdeInstaller(fileName: "apache-ode-war-1.3.6.zip",
                odeName: "apache-ode-war-1.3.6",
                serverDir: getServerPath()).install()
    }
}

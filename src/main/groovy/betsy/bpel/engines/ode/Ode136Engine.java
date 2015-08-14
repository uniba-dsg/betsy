package betsy.bpel.engines.ode;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Ode136Engine extends OdeEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "ode", "1.3.6");
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public void install() {
        new OdeInstaller(getServerPath(), "apache-ode-war-1.3.6.zip").install();
    }

}

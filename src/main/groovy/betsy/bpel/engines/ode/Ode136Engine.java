package betsy.bpel.engines.ode;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.time.LocalDate;

public class Ode136Engine extends OdeEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "ode", "1.3.6", LocalDate.of(2013,10,12));
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public void install() {
        new OdeInstaller(getServerPath(), "apache-ode-war-1.3.6.zip").install();
    }

}

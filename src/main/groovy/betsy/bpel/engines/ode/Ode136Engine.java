package betsy.bpel.engines.ode;

import java.nio.file.Path;
import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.util.ClasspathHelper;

public class Ode136Engine extends OdeEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "ode", "1.3.6", LocalDate.of(2013,10,12), "Apache-2.0");
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public void install() {
        new OdeInstaller(getServerPath(), "apache-ode-war-1.3.6.zip").install();
    }

}

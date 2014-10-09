package betsy.bpel.engines.ode;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class Ode136Engine extends OdeEngine {
    @Override
    public String getName() {
        return "ode136";
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public void install() {
        OdeInstaller installer = new OdeInstaller();
        installer.setFileName("apache-ode-war-1.3.6.zip");
        installer.setOdeName("apache-ode-war-1.3.6");
        installer.setServerDir(getServerPath());
        installer.install();
    }

}

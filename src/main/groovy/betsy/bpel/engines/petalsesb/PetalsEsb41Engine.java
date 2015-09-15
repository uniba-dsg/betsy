package betsy.bpel.engines.petalsesb;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PetalsEsb41Engine extends PetalsEsbEngine {
    @Override
    public String getName() {
        return "petalsesb41";
    }

    public String getPetalsFolderName() {
        return "petals-esb-4.1";
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/petalsesb");
    }

    @Override
    public void install() {
        PetalsEsbInstaller installer = new PetalsEsbInstaller(this);
        installer.setServerDir(Paths.get("server/petalsesb41"));
        installer.setFileName("petals-esb-distrib-4.1.0.zip");
        installer.setTargetEsbInstallDir(Paths.get("server/petalsesb41/petals-esb-4.1/install"));
        installer.setBpelComponentPath(Paths.get("server/petalsesb41/petals-esb-distrib-4.1.0/esb-components/petals-se-bpel-1.1.0.zip"));
        installer.setSoapComponentPath(Paths.get("server/petalsesb41/petals-esb-distrib-4.1.0/esb-components/petals-bc-soap-4.2.0.zip"));
        installer.setSourceFile(Paths.get("server/petalsesb41/petals-esb-distrib-4.1.0/esb/petals-esb-4.1.zip"));
        installer.install();
    }

}

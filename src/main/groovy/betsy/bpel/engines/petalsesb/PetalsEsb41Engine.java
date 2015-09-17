package betsy.bpel.engines.petalsesb;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PetalsEsb41Engine extends PetalsEsbEngine {

    private static final Logger LOGGER = Logger.getLogger(PetalsEsb41Engine.class);

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
    public Path getPetalsCliBinFolder() {return getServerPath().resolve("petals-cli-2.0.0/bin");}

    @Override
    public void shutdown() {
        try {
            // create shutdown command script and execute it via the cli
            FileTasks.createFile(getPetalsCliBinFolder().resolve("shutdown-petals.script"), "connect\nstop container --shutdown\nexit");

            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.bat")).values("shutdown-petals.script"));

            ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getPetalsCliBinFolder().resolve("petals-cli.sh").toString()));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.sh")).values("shutdown-petals.script"));
        } catch (Exception e) {
            LOGGER.info("COULD NOT STOP ENGINE " + getName(), e);
        }
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
        installer.setCliFile(Paths.get("server/petalsesb41/petals-esb-distrib-4.1.0/esb/petals-cli-2.0.0.zip"));
        installer.install();
    }

}

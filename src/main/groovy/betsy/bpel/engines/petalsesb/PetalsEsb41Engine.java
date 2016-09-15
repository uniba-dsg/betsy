package betsy.bpel.engines.petalsesb;

import java.nio.file.Path;
import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

public class PetalsEsb41Engine extends PetalsEsbEngine {

    private static final Logger LOGGER = Logger.getLogger(PetalsEsb41Engine.class);

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "petalsesb", "4.1", LocalDate.of(2012, 7, 6), "LGPL 2.1+");
    }

    public String getPetalsFolderName() {
        return "petals-esb-4.1";
    }

    @Override
    public Path getPetalsCliBinFolder() {
        return getServerPath().resolve("petals-cli-2.0.0/bin");
    }

    @Override
    public void shutdown() {
        try {
            // create shutdown command script and execute it via the cli
            FileTasks.createFile(getPetalsCliBinFolder().resolve("shutdown-petals.script"), "connect\nstop container --shutdown\nexit");

            ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.bat")).values("shutdown-petals.script"));

            ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("chmod").values("+x", getPetalsCliBinFolder().resolve("petals-cli.sh").toString()));
            ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("sync"));
            ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(getPetalsCliBinFolder(), getPetalsCliBinFolder().resolve("petals-cli.sh")).values("shutdown-petals.script"));
        } catch (Exception e) {
            LOGGER.info("COULD NOT STOP ENGINE " + getName(), e);
        }
    }

    @Override
    public void install() {
        PetalsEsbInstaller installer = new PetalsEsbInstaller();
        installer.setServerDir(getServerPath());
        installer.setFileName("petals-esb-distrib-4.1.0.zip");
        installer.setTargetEsbInstallDir(getServerPath().resolve("petals-esb-4.1/install"));
        installer.setBpelComponentPath(getServerPath().resolve("petals-esb-distrib-4.1.0/esb-components/petals-se-bpel-1.1.0.zip"));
        installer.setSoapComponentPath(getServerPath().resolve("petals-esb-distrib-4.1.0/esb-components/petals-bc-soap-4.2.0.zip"));
        installer.setSourceFile(getServerPath().resolve("petals-esb-distrib-4.1.0/esb/petals-esb-4.1.zip"));
        installer.setCliFile(getServerPath().resolve("petals-esb-distrib-4.1.0/esb/petals-cli-2.0.0.zip"));
        installer.setPetalsBinFolder(getPetalsBinFolder());
        installer.install();
    }

}

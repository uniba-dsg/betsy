package betsy.common.reporting;

import java.nio.file.Path;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

public class JUnitHtmlReports {

    private static final Logger log = Logger.getLogger(JUnitHtmlReports.class);

    /**
     * tests folder
     */
    private final Path path;

    public JUnitHtmlReports(Path path) {
        this.path = path;
    }

    public void create() {
        log.info("creating reporting ant scripts");
        Path buildXmlFile = path.resolve("build.xml");
        FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/build.xml.template"), buildXmlFile);
        FileTasks.replaceTokenInFile(buildXmlFile, "PROJECT_NAME", path.toString());

        log.info("executing reporting ant scripts");
        ConsoleTasks.setupAnt(getAntPath());
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(path, getAntPath().toAbsolutePath().resolve("ant").toString()));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(path, getAntPath().toAbsolutePath().resolve("ant").toString()));
    }

    private Path getAntPath() {
        return Configuration.getAntHome().resolve("bin");
    }
}

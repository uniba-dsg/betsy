package betsy.common.reporting;

import betsy.common.config.Configuration;
import betsy.common.tasks.ConsoleTasks;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import org.apache.log4j.Logger;

import java.nio.file.Path;

public class JUnitHtmlReports {

    public JUnitHtmlReports(Path path) {
        this.path = path;
    }

    public void create() {
        Path antBinFolder = Configuration.getAntHome().resolve("bin").toAbsolutePath();

        log.info("creating reporting ant scripts");

        Path buildXmlFile = path.resolve("build.xml");
        FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/build.xml.template"), buildXmlFile);
        FileTasks.replaceTokenInFile(buildXmlFile, "PROJECT_NAME", path.toString());

        log.info("executing reporting ant scripts");
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(path, antBinFolder.resolve("ant.bat").toString()));

        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(path, "chmod").values("+x", antBinFolder.resolve("ant").toString()));
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build(path, antBinFolder.resolve("ant").toString()));
    }

    private static final Logger log = Logger.getLogger(JUnitHtmlReports.class);

    /**
     * tests folder
     */
    private final Path path;
}

package soapui;

import betsy.tasks.FileTasks;
import com.eviware.soapui.tools.SoapUITestCaseRunner;
import org.apache.log4j.Logger;

import java.nio.file.Path;

/**
 * Runs soap ui tests programmatically within the current JVM. Requires soapUI to be present in the class path.
 */
public class SoapUiRunner {

    private static final Logger log = Logger.getLogger(SoapUiRunner.class);

    private final Path soapUiProjectFile;
    private final Path reportingDirectory;

    public SoapUiRunner(Path soapUiProjectFile, Path reportingDirectory) {
        this.soapUiProjectFile = soapUiProjectFile;
        this.reportingDirectory = reportingDirectory;

        FileTasks.assertFile(soapUiProjectFile);
        FileTasks.assertDirectory(reportingDirectory);
    }

    public void run() {
        SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        runner.setProjectFile(soapUiProjectFile.toString());
        runner.setJUnitReport(true);// j
        runner.setOutputFolder(reportingDirectory.toString());// f
        runner.setPrintAlertSiteReport(false);
        runner.setIgnoreError(true);
        runner.setExportAll(true);// a

        try {
            runner.run();
        } catch (Exception ignore) {
            log.error("Exception occured during Test " + reportingDirectory + ". See test results for more information.");
        }

    }
}

package soapui;

import com.eviware.soapui.tools.SoapUITestCaseRunner;
import org.apache.log4j.Logger;

/**
 * Runs soap ui tests programmatically within the current JVM. Requires soapUI to be present in the class path.
 */
public class SoapUiRunner {
    private static final Logger log = Logger.getLogger(SoapUiRunner.class);
    private String soapUiProjectFile;
    private String reportingDirectory;

    public void run() {
        SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        runner.setProjectFile(soapUiProjectFile);
        runner.setJUnitReport(true);// j
        runner.setOutputFolder(reportingDirectory);// f
        runner.setPrintAlertSiteReport(false);
        runner.setIgnoreError(true);
        runner.setExportAll(true);// a

        try {
            runner.run();
        } catch (Exception ignore) {
            log.error("Exception occured during Test " + reportingDirectory + ". See test results for more information.");
        }

    }

    public String getSoapUiProjectFile() {
        return soapUiProjectFile;
    }

    public void setSoapUiProjectFile(String soapUiProjectFile) {
        this.soapUiProjectFile = soapUiProjectFile;
    }

    public String getReportingDirectory() {
        return reportingDirectory;
    }

    public void setReportingDirectory(String reportingDirectory) {
        this.reportingDirectory = reportingDirectory;
    }
}

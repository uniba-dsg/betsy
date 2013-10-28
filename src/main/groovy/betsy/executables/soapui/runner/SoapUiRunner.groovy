package betsy.executables.soapui.runner

import com.eviware.soapui.tools.SoapUITestCaseRunner

/**
 * Runs soap ui tests programmatically within the current JVM. Requires soapUI to be present in the class path.
 */
class SoapUiRunner {

    AntBuilder ant = new AntBuilder()

    String soapUiProjectFile
    String reportingDirectory

    public void run() {
        SoapUITestCaseRunner runner = new SoapUITestCaseRunner();
        runner.setProjectFile(soapUiProjectFile);
        runner.setJUnitReport(true) // j
        runner.setOutputFolder(reportingDirectory) // f
        runner.setPrintAlertSiteReport(false);
        runner.setIgnoreError(true)
        runner.setExportAll(true) // a

        try {
            runner.run()
        } catch (Exception ignore) {
            ant.echo message: "Exception occured during Test ${reportingDirectory}. See test results for more information.", level: "error"
        }
    }
}

package betsy.executables.reporting;

import betsy.model.TestSuite;

public class Reporter {

    private final TestSuite tests;

    public Reporter(TestSuite tests) {
        this.tests = tests;
    }

    public void createReports() {
        MessageExchangesIntoSoapUIReportsMerger merger = new MessageExchangesIntoSoapUIReportsMerger();

        merger.setTests(tests);
        merger.merge();
        JUnitHtmlReports reports = new JUnitHtmlReports();

        reports.setPath(tests.getPath());
        reports.create();
        JUnitXmlResultToCsvRow row = new JUnitXmlResultToCsvRow();


        row.setXml(tests.getJUnitXMLFilePath());
        row.setCsv(tests.getCsvFilePath());
        row.create();
    }

}

package betsy.executables.reporting;

import betsy.data.TestSuite;

public class Reporter {
    private TestSuite tests;

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

    public TestSuite getTests() {
        return tests;
    }

    public void setTests(TestSuite tests) {
        this.tests = tests;
    }
}

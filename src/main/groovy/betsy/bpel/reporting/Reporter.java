package betsy.bpel.reporting;

import betsy.common.reporting.JUnitHtmlReports;
import betsy.common.reporting.JUnitXmlResultToCsvRow;
import betsy.common.model.TestSuite;

public class Reporter {

    private final TestSuite tests;

    public Reporter(TestSuite tests) {
        this.tests = tests;
    }

    public void createReports() {
        MessageExchangesIntoSoapUIReportsMerger merger = new MessageExchangesIntoSoapUIReportsMerger();

        merger.setTests(tests);
        merger.merge();
        new JUnitHtmlReports(tests.getPath()).create();
        JUnitXmlResultToCsvRow row = new JUnitXmlResultToCsvRow();


        row.setXml(tests.getJUnitXMLFilePath());
        row.setCsv(tests.getCsvFilePath());
        row.create();
    }

}

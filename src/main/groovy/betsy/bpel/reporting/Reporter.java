package betsy.bpel.reporting;

import betsy.bpel.model.BPELTestSuite;
import betsy.common.reporting.JUnitHtmlReports;
import betsy.common.reporting.JUnitXmlResultToCsvRow;

public class Reporter {

    private final BPELTestSuite tests;

    public Reporter(BPELTestSuite tests) {
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

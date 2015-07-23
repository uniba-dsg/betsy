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
        new MessageExchangesIntoSoapUIReportsMerger(tests).merge();



        new JUnitHtmlReports(tests.getPath()).create();
        new JUnitXmlResultToCsvRow(tests.getJUnitXMLFilePath(), tests.getCsvFilePath()).create();
    }

}

package betsy.bpmn.reporting;

import betsy.bpmn.model.BPMNTestSuite;
import betsy.common.reporting.JUnitHtmlReports;
import betsy.common.reporting.JUnitXmlResultToCsvRow;

public class BPMNReporter {

    private final BPMNTestSuite tests;

    public BPMNReporter(BPMNTestSuite tests) {
        this.tests = tests;
    }

    public void createReports() {
        JUnitHtmlReports reports = new JUnitHtmlReports();
        reports.setPath(tests.getPath());
        reports.create();

        JUnitXmlResultToCsvRow row = new JUnitXmlResultToCsvRow();
        row.setXml(tests.getJUnitXMLFilePath());
        row.setCsv(tests.getCsvFilePath());
        row.create();
    }

}

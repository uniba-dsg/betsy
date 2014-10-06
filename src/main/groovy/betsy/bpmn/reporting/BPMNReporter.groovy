package betsy.bpmn.reporting

import betsy.bpmn.model.BPMNTestSuite
import betsy.common.executables.reporting.JUnitHtmlReports
import betsy.common.executables.reporting.JUnitXmlResultToCsvRow

class BPMNReporter {
    BPMNTestSuite tests

    void createReports(){
        new JUnitHtmlReports(path: tests.path).create()
        new JUnitXmlResultToCsvRow(xml: tests.JUnitXMLFilePath, csv: tests.csvFilePath).create()
    }
}

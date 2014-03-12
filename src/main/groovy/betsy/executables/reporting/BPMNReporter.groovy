package betsy.executables.reporting

import betsy.data.BPMNTestSuite

class BPMNReporter {
    BPMNTestSuite tests

    void createReports(){
        new JUnitHtmlReports(path: tests.path).create()
        new JUnitXmlResultToCsvRow(xml: tests.JUnitXMLFilePath, csv: tests.csvFilePath).create()
    }
}

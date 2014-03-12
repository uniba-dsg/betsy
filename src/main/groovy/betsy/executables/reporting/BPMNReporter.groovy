package betsy.executables.reporting

import betsy.data.BPMNTestSuite

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran
 * Date: 12.03.14
 * Time: 12:24
 */
class BPMNReporter {
    BPMNTestSuite tests

    void createReports(){
        new JUnitHtmlReports(path: tests.path).create()
        new JUnitXmlResultToCsvRow(xml: tests.JUnitXMLFilePath, csv: tests.csvFilePath).create()
    }
}

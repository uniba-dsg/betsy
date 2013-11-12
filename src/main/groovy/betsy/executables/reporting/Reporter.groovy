package betsy.executables.reporting

import ant.tasks.AntUtil
import betsy.data.TestSuite

class Reporter {

    final AntBuilder ant = AntUtil.builder()

    TestSuite tests

    void createReports() {
        new MessageExchangesIntoSoapUIReportsMerger(tests: tests).merge()
        new JUnitHtmlReports(path: tests.path).create()
        new JUnitXmlResultToCsvRow(xml: tests.JUnitXMLFilePath, csv: tests.csvFilePath).create()
        new TestStepDurationCsvReports(tests: tests, csv: tests.csvDurationFilePath).create()
    }

}

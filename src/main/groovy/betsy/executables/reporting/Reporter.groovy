package betsy.executables.reporting

import betsy.data.TestSuite

class Reporter {

    AntBuilder ant = new AntBuilder()

    TestSuite tests

    void createReports() {
        new MessageExchangesIntoSoapUIReportsMerger(tests: tests, ant: ant).merge()
        new JUnitHtmlReports(path: tests.path, ant: ant).create()
        new JUnitXmlResultToCsvRow(xml: tests.JUnitXMLFilePath, csv: tests.csvFilePath).create()
        new TestStepDurationCsvReports(tests: tests, csv: tests.csvDurationFilePath).create()
    }

}

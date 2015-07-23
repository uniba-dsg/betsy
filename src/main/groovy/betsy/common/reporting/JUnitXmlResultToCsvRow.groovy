package betsy.common.reporting

import java.nio.file.Path

class JUnitXmlResultToCsvRow {

    /**
     * path to junit result xml file (READ)
     */
    Path xml

    /**
     * path to resulting csv file (WRITE)
     */
    Path csv

    public void create() {
        // read
        List<CsvRow> rows = readRows()

        // sort
        Collections.sort(rows);

        // write
        writeRows(rows)
    }

    private void writeRows(List<CsvRow> rows) {
        csv.toFile().withPrintWriter { w ->
            for (CsvRow row : rows) {
                w.println(row.toRow());
            }
        }
    }

    private List<CsvRow> readRows() {
        List<CsvRow> rows = new LinkedList<>();
        def testsuites = new XmlSlurper(false, false).parse(xml.toFile())
        testsuites.testsuite.each { testSuite ->

            String pkg = testSuite.@package.text()

            CsvRow csvRow = new CsvRow(
                    name: testSuite.@name.text(),
                    engine: pkg.replaceAll("soapui.", "").tokenize(".").first(),
                    group: pkg.tokenize(".").last(),
                    tests: testSuite.@tests.text(),
                    totalFailures: testSuite.@failures.text(),
                    deployable: (testSuite.testcase.error.@message.text().contains("but got [ERROR_deployment")
                            || testSuite.testcase.failure.text().contains("Test Availability of WSDL Failed")) ? 0 : 1
            )
            rows.add(csvRow)
        }
        rows
    }

}

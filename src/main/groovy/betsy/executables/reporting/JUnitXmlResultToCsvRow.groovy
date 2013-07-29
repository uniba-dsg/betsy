package betsy.executables.reporting

class JUnitXmlResultToCsvRow {

    /**
     * Represents one row in the resulting csv file
     *
     * TEST NAME; ENGINE NAME; GROUP NAME; SUCCESS FLAG; NUMBER OF FAILURES; NUMBER OF TESTS
     *
     * SUCCESS FLAG: 1 for successful, 0 for not successful
     */
    static class CsvRow {
        String name
        String engine
        String group
        String totalFailures
        String tests

        String getBinaryResult() {
            totalFailures == "0" ? "1" : "0"
        }

        String toRow() {
            [name, engine, group, binaryResult, totalFailures, tests].join(";")
        }
    }

    /**
     * path to junit result xml file (READ)
     */
    String xml

    /**
     * path to resulting csv file (WRITE)
     */
    String csv

    public void create() {

        new File(csv).withPrintWriter { w ->
            def testsuites = new XmlSlurper(false, false).parse(new File(xml))
            testsuites.testsuite.each { testSuite ->

                String pkg = testSuite.@package.text()

                CsvRow csvRow = new CsvRow(
                        name: testSuite.@name.text(),
                        engine: pkg.replaceAll("soapui.", "").tokenize(".").first(),
                        group: pkg.tokenize(".").last(),
                        tests: testSuite.@tests.text(),
                        totalFailures: testSuite.@failures.text()
                )

                w.println(csvRow.toRow())
            }
        }

    }

}

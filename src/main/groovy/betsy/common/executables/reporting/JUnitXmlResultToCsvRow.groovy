package betsy.common.executables.reporting

import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

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
        String deployable

        String getBinaryResult() {
            totalFailures.equals("0") ? "1" : "0"
        }

        String toRow() {
            return Stream.of(name, engine, group, binaryResult, totalFailures, tests, deployable).collect(Collectors.joining(";"));
        }
    }

    /**
     * path to junit result xml file (READ)
     */
    Path xml

    /**
     * path to resulting csv file (WRITE)
     */
    Path csv

    public void create() {

        csv.toFile().withPrintWriter { w ->
            def testsuites = new XmlSlurper(false, false).parse(xml.toFile())
            testsuites.testsuite.each { testSuite ->

                String pkg = testSuite.@package.text()

                CsvRow csvRow = new CsvRow(
                        name: testSuite.@name.text(),
                        engine: pkg.replaceAll("soapui.", "").tokenize(".").first(),
                        group: pkg.tokenize(".").last(),
                        tests: testSuite.@tests.text(),
                        totalFailures: testSuite.@failures.text(),
                        deployable: testSuite.testcase.failure.text().contains("Test Availabilty of WSDL Failed") ? 0 : 1
                )

                w.println(csvRow.toRow())
            }
        }

    }

}

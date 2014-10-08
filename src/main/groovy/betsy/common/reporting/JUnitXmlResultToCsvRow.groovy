package betsy.common.reporting

import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

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

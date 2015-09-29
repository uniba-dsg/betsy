package betsy.common.reporting

import betsy.common.tasks.FileTasks

import java.nio.file.Path

public class JUnitXmlResultReader {

    private final Path xml;

    public JUnitXmlResultReader(Path xml) {
        FileTasks.assertFile(xml);
        this.xml = Objects.requireNonNull(xml);
    }

    public List<CsvRow> readRows() {
        List<CsvRow> result = new LinkedList<>();
        def testsuites = new XmlSlurper(false, false).parse(xml.toFile());
        testsuites.testsuite.each { testSuite ->

            String pkg = testSuite.@package.text();

            CsvRow csvRow = new CsvRow(
                    name: testSuite.@name.text(),
                    engine: pkg.replaceAll("soapui.", "").tokenize(".").first(),
                    group: pkg.tokenize(".").last(),
                    tests: testSuite.@tests.text(),
                    totalFailures: testSuite.@failures.text(),
                    deployable: (testSuite.testcase.error.@message.text().contains("but got [ERROR_deployment")
                            || testSuite.testcase.failure.text().contains("Test Availability of WSDL Failed")) ? 0 : 1
            );
            result.add(csvRow);
        }

        return result;
    }

}

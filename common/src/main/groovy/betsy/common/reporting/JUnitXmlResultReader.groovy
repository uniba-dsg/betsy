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
                    testSuite.@name.text(),
                    pkg.replaceAll("soapui.", "").tokenize(".").first(),
                    pkg.tokenize(".").last(),
                    Integer.parseInt(testSuite.@failures.text()),
                    Integer.parseInt(testSuite.@tests.text()),
                    !(testSuite.testcase.error.@message.text().contains("but got [ERROR_deployment")
                            || testSuite.testcase.failure.text().contains("Test Availability of WSDL Failed"))
            );

            result.add(csvRow);
        }

        return result;
    }

}

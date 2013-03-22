package betsy.executables.reporting

import betsy.data.TestSuite

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import betsy.data.Process


class MessageExchangesIntoSoapUIReportsMerger {

    AntBuilder ant = new AntBuilder()
    TestSuite tests

    public void merge() {
        tests.engines.each { engine ->
            engine.processes.each { process ->
                mergeMessageExchangeProtocolsIntoJUnitReportForProcess(process)
            }
        }
    }

    private void mergeMessageExchangeProtocolsIntoJUnitReportForProcess(Process process) {
        Path junitXml = findFileInReports(process.targetReportsPath, "*.xml")
        def testsuites = new XmlParser().parse(junitXml.toFile())

        testsuites.testcase.each { testcase ->

            String testcase_name_normalized = testcase.@name.replaceAll("-", "").replaceAll(" ", "_")

            if (testcase.failure) {
                Node failure = testcase.failure[0]

                Path txt = findFileInReports(process.targetReportsPath, "*${testcase_name_normalized}*FAILED.txt")

                String previousText = failure.text()
                String failureText = txt.toFile().readLines().join("""
""")
                String newText = """$previousText


$failureText"""

                failure.value = newText
            }
        }
        new XmlNodePrinter(new PrintWriter(new FileWriter(junitXml.toFile()))).print(testsuites)
    }

    private Path findFileInReports(String dir, String glob) {
        ant.echo message: "Finding files in dir ${dir} with pattern ${glob}"
        Files.newDirectoryStream(Paths.get(dir), glob).iterator().next()
    }
}

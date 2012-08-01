package betsy.executables.reporting

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import betsy.data.TestSuite
import betsy.data.Process

import betsy.executables.reporting.csv.CsvToReports

class Reporter  {

    AntBuilder ant = new AntBuilder()

    TestSuite tests

    void createReports() {
        mergeMessageExchangeProtocolsIntoJUnitReports()
        createHtmlReports()
        createResultsCsv()
        createLatexTable()
    }

    private void createLatexTable() {
        CsvToReports csvToReports = new CsvToReports(file: tests.csvFilePath)
        csvToReports.load()
        csvToReports.toHtmlReport(tests.reportsPath + "/results.html")
        csvToReports.toLatexReport(tests.reportsPath + "/results.tex")
    }

    private void createResultsCsv() {
        new XmlToCsv(xml: new File(tests.reportsPath + "/TESTS-TestSuites.xml")).toCsv(new File(tests.csvFilePath))
    }

    private void createHtmlReports() {
        ant.echo(message: "creating reporting ant scripts")
        ant.echo(message: createAntReportFile(tests.path), file: "${tests.path}/build.xml")

        ant.echo(message: "executing reporting ant scripts")
        ant.exec(executable: "cmd", dir: tests.path) {
            arg(value: "/c")
            arg(value: "ant")
        }
    }

    private void mergeMessageExchangeProtocolsIntoJUnitReports() {
        tests.engines.each { engine ->
            engine.processes.each {process ->
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

    private String createAntReportFile(String name) {
        """
<project name="${name}" default="reports">

    <target name="reports">

        <mkdir dir="reports"/>
        <junitreport todir="reports">
            <fileset dir=".">
                <include name="**/TEST-*.xml"/>
            </fileset>
            <report format="frames" todir="reports/html"/>
        </junitreport>

    </target>

</project>"""
    }
}

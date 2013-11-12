package betsy.executables.reporting

import ant.tasks.AntUtil
import betsy.data.TestSuite
import org.apache.log4j.Logger

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import betsy.data.BetsyProcess


class MessageExchangesIntoSoapUIReportsMerger {

    private static final Logger log = Logger.getLogger(MessageExchangesIntoSoapUIReportsMerger.class)

    final AntBuilder ant = AntUtil.builder()
    TestSuite tests

    public void merge() {
        tests.engines.each { engine ->
            engine.processes.each { process ->
                mergeMessageExchangeProtocolsIntoJUnitReportForProcess(process)
            }
        }
    }

    private void mergeMessageExchangeProtocolsIntoJUnitReportForProcess(BetsyProcess process) {
        Path junitXml = findFileInReports(process.targetReportsPath, "*.xml")
        if(junitXml == null){
            log.warn "Cannot merge xml report from process ${process.name} as there is no xml report"
            return
        }

        def testsuites = new XmlParser().parse(junitXml.toFile())

        testsuites.testcase.each { testcase ->

            String testcase_name_normalized = testcase.@name.replaceAll("-", "").replaceAll(" ", "_")

            if (testcase.failure) {
                Node failure = testcase.failure[0]

                Path txt = findFileInReports(process.targetReportsPath, "*${testcase_name_normalized}*FAILED.txt")

                String previousText = failure.text()
                String failureText = txt.toFile().readLines().join("""
""")
                // hack for bad encoding issues in jenkins
                failureText = failureText.replaceAll("ü","ue").replaceAll("ä","ae").replaceAll("ö","oe")

                String newText = """$previousText


$failureText"""

                failure.value = newText
            }
        }
        new XmlNodePrinter(new PrintWriter(new FileWriter(junitXml.toFile()))).print(testsuites)
    }

    private Path findFileInReports(Path reportsDirectory, String glob) {
        log.info "Finding files in dir ${reportsDirectory} with pattern ${glob}"
        if(!Files.exists(reportsDirectory)) {
            log.warn "Folder ${reportsDirectory} does not exist"

            return null
        }

        Files.newDirectoryStream(reportsDirectory, glob).iterator().next()
    }
}

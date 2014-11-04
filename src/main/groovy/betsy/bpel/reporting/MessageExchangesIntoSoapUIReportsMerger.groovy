package betsy.bpel.reporting

import betsy.bpel.model.BetsyProcess
import betsy.bpel.engines.Engine
import betsy.bpel.model.BPELTestSuite
import betsy.common.tasks.FileTasks
import org.apache.log4j.Logger

import java.nio.file.Path

class MessageExchangesIntoSoapUIReportsMerger {

    private static final Logger log = Logger.getLogger(MessageExchangesIntoSoapUIReportsMerger.class)

    BPELTestSuite tests

    public void merge() {
        for(Engine engine : tests.engines) {
            for(BetsyProcess process : engine.processes) {
                mergeMessageExchangeProtocolsIntoJUnitReportForProcess(process)
            }
        }
    }

    private static void mergeMessageExchangeProtocolsIntoJUnitReportForProcess(BetsyProcess process) {
        Path junitXml = FileTasks.findFirstMatchInFolder(process.targetReportsPath, "*.xml")
        if (junitXml == null) {
            log.warn "Cannot merge xml report from process ${process.name} as there is no xml report"
            return
        }

        def testsuites = new XmlParser().parse(junitXml.toFile())

        testsuites.testcase.each { testcase ->

            String testcase_name_normalized = testcase.@name.replaceAll("-", "").replaceAll(" ", "_")

            if (testcase.failure) {
                Node failure = testcase.failure[0]

                Path txt = FileTasks.findFirstMatchInFolder(process.targetReportsPath, "*${testcase_name_normalized}*FAILED.txt")

                String previousText = failure.text()
                String failureText = txt.toFile().text
                // hack for bad encoding issues in jenkins
                failureText = failureText.replaceAll("ü", "ue").replaceAll("ä", "ae").replaceAll("ö", "oe")

                String newText = """$previousText


$failureText"""

                failure.value = newText
            }
        }
        new XmlNodePrinter(new PrintWriter(new FileWriter(junitXml.toFile()))).print(testsuites)
    }


}

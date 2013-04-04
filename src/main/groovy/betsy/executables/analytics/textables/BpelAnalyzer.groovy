package betsy.executables.analytics.textables

import betsy.data.Process
import betsy.data.TestAssertion
import betsy.data.assertions.ExitAssertion
import betsy.data.assertions.NotDeployableAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.XpathTestAssertion
import configuration.processes.Processes
import groovy.text.SimpleTemplateEngine

class BpelAnalyzer {

    String output
    private int tableCounter = 0

    public void printLatexTableWithResult(String name, String resultString) {
        Process process = getProcess(name)

        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(new File("src\\main\\groovy\\betsy\\executables\\analytics\\textables\\BpelAnalyzer.template").text).make([
                "process": process,
                "name": process.getBpelFileNameWithoutExtension(),
                "resultString": resultString,
                "uncommonNodes": BpelNodeCounter.getUncommonNames(new File(process.bpelFilePath)),
                "analyzer": this
        ])

        new File(output).withWriterAppend("UTF-8") {
            it.write(template.toString())

            tableCounter++
            if (tableCounter % 18 == 0) {
                it.write "\\clearpage"
            }
        }
    }

    static String getAssertionsDescription(List<TestAssertion> assertions) {
        assertions.collect {
            if (it instanceof NotDeployableAssertion) {
                "NotDeployable"
            } else if (it instanceof SoapFaultTestAssertion) {
                SoapFaultTestAssertion assertion = (SoapFaultTestAssertion) it
                "fault: ${assertion.faultString}"
            } else if (it instanceof XpathTestAssertion) {
                XpathTestAssertion assertion = (XpathTestAssertion) it
                "output: ${assertion.output}"
            } else if (it instanceof ExitAssertion) {
                "Exit"
            } else {
                throw new IllegalStateException("given assertion did not match")
            }
        }.join("; ")
    }

    private static Process getProcess(String name) {
        new Processes().ALL.find { it.bpelFileName == name }
    }


}
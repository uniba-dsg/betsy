package betsy.executables.analytics

import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase
import betsy.executables.analytics.model.CsvReport
import betsy.executables.analytics.model.Engine
import betsy.executables.analytics.model.Group
import betsy.executables.analytics.model.Test
import configuration.BPMNProcessRepository
import org.json.JSONObject

import java.nio.file.Path

class BPMNLatexSerializer {

    private int tableCounter = 0

    CsvReport csvReport
    Path fileCompact
    Path fileDetailed

    public void serializeCompactResults(){
        PrintWriter writer = new PrintWriter(fileCompact.toString())
        Collection<Engine> engines = csvReport.engines
        csvReport.getGroups().each { group ->
            printCompactHeader(group, engines, writer)
            group.tests.each { test ->
                printCompactBody(test, writer)
            }
            printCompactFooter(writer)
        }
        writer.close()
    }

    public void serializeDetailedDescriptions(){
        PrintWriter writer = new PrintWriter(fileDetailed.toString())
        List<BPMNProcess> processes = new BPMNProcessRepository().getByName("ALL")
        csvReport.getGroups().each { group ->
            group.tests.each { test ->
                BPMNProcess process = processes.find {it.name == test.name}
                printDetailedHeader(writer)
                printDetailedBody(process, test, writer)
                printDetailedFooter(test.name, writer)
            }
        }
        writer.close()
    }

    private void printCompactHeader(Group group, Collection<Engine> engines, PrintWriter writer){
        writer.println("\\subsection{${group.name}}")
        writer.write("\\begin{longtable}{|l|")
        engines.each {writer.write("c|")}
        writer.write("}\n")
        writer.println("\\hline")
        writer.println("\\textbf{${group.name}} & " + engines.collect { "\\textbf{$it.name}"}.join(" & ") + "\\\\\\hline")
        writer.println("\\endhead")
    }

    private void printCompactBody(Test test, PrintWriter writer){
        writer.write(test.name)
        test.engineToResult.each { engine, value ->
            writer.write(" & ${value.partial.toNormalizedSymbol()}")
        }
        writer.write("\\\\\\hline\n")
    }

    private void printCompactFooter(PrintWriter writer){
        writer.println("\\end{longtable}")
    }

    private void printDetailedHeader(PrintWriter writer){
        writer.println("\\begin{table}[htbp]")
        writer.println("\\footnotesize")
        writer.println("\\begin{longtable}{| p{5cm} | p{11,5cm} |}")
        writer.println("\\hline")
    }

    private void printDetailedBody(BPMNProcess process, Test test, PrintWriter writer){
        writer.println("Process name & ${process.name}\\\\\\hline")
        writer.println("Group & ${process.group}\\\\\\hline")
        writer.println("Description & ${process.description}\\\\\\hline")
        process.testCases.each { testCase ->
            printTestCase(testCase, writer)
        }
        printResultSection(test, csvReport.engines, writer)
    }

    private void printTestCase(BPMNTestCase testCase, PrintWriter writer){
        String assertions = testCase.assertions.join(", ")
        String input = "-"
        if(testCase.variables.has("test")){
            input = ((JSONObject)testCase.variables.get("test")).get("value")
        }
        String options = ""
        if(testCase.selfStarting){
            options += "selfStarting "
        }
        if(testCase.delay){
            options += "Delay: " + testCase.delay + "ms "
        }
        writer.println("Test case ${testCase.number} & ")
        writer.println("\\begin{tabular}[t]{|p{1cm}|p{7.5cm}|p{1.5cm}|}")
        writer.println("\\hline")
        writer.println("input & assertions & options\\\\\\hline")
        writer.println(input + " & " + assertions + " & " + options + "\\\\\\hline")
        writer.println("\\end{tabular}")
        writer.println("\\\\\\hline")
    }

    private void printResultSection(Test test, Collection<Engine> engines,PrintWriter writer){
        writer.println("Support & ")
        writer.write("\\begin{tabular}[t]{|")
        engines.each {writer.write("p{1,5cm}|")}
        writer.write("}\n")
        writer.println("\\hline")
        writer.println(engines.collect {it.name}.join(" & ") + "\\\\\\hline")
        Collection<String> results = new ArrayList<>()
        test.engineToResult.each { engine, value ->
            results.add(value.partial.toNormalizedSymbol())
        }
        writer.println("${results.join(" & ")}\\\\\\hline")
        writer.println("\\end{tabular}")
        writer.println("\\\\\\hline")
    }

    private void printDetailedFooter(String name, PrintWriter writer){
        writer.println("\\end{longtable}")
        writer.println("\\caption{${name} Test}")
        writer.println("\\label{test:${name}}")
        writer.println("\\end{table}")
        writer.println("\\setcounter{table}{\\value{table}-1}")
        writer.println("")

        tableCounter++
        if (tableCounter % 18 == 0) {
            writer.println("\\clearpage")
        }
    }
}

package betsy.executables.analytics

import betsy.executables.reporting.bpel.BpelAnalyzer
import betsy.executables.analytics.model.CsvReport
import betsy.executables.analytics.model.Engine
import betsy.executables.analytics.model.Result

class LatexAnalytics {

    CsvReport report

    void toLatexReport(String filename) {
        BpelAnalyzer bpelAnalyzer = new BpelAnalyzer(new File("src/main/tests"),new PrintStream(new File("test/reports/tables.tex")))
        new File(filename).withPrintWriter { writer ->

            report.nameToGroup.each { groupName, group ->

                writer.println("\\subsection{${group.name}}")

                writer.println("\\begin{longtable}{|l|c|c|c|c|c|}")
                writer.println("\\hline")

                writer.println("\\textbf{${group.name}} & " + report.nameToEngine.keySet().collect { "\\textbf{$it}" }.join(" & ") + "\\\\\\hline")
                writer.println("\\endhead")

                group.tests.each { test ->

                    writer.write(test.fullName)

                    test.engineToResult.each {engine, value ->
                        writer.write(" & ${value.partial.toNormalizedSymbol()}")
                    }

                    bpelAnalyzer.printLatexTableWithResult("${test.fullName}.bpel",getResultString(test.engineToResult))

                    writer.write("\\\\\\hline\n")
                }
                writer.println("\\end{longtable}")
            }

        }

    }

    private String getResultString(SortedMap<Engine, Result> engines){
        StringBuffer result = new StringBuffer()
        engines.each {engine, value ->
            result.append(value.partial.toNormalizedSymbol())
            if(!engine.name.equals("petalsesb")){
                result.append(" & ")
            }
        }
        return result.toString()
    }

}

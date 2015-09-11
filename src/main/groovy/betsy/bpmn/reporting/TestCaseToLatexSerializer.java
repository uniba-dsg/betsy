package betsy.bpmn.reporting;

import betsy.bpmn.model.*;
import betsy.common.model.TestAssertion;
import betsy.common.model.TestCase;
import betsy.common.model.TestStep;
import configuration.bpmn.BPMNProcessRepository;

import java.util.List;
import java.util.Optional;

/**
 * Creates a LaTeX table from a list of processes and writes it to standard output
 */
public class TestCaseToLatexSerializer {

    private final String TABLE_NEWLINE = "\\\\";

    private final String ROW_COLOUR = "\\myrowcolour";

    private final String COLOUR_NEXT_ROW = TABLE_NEWLINE + ROW_COLOUR;

    public static void main(String... args) {
        new TestCaseToLatexSerializer().buildTableFromProcesses(new BPMNProcessRepository().getByName("ALL"));
    }

    private void buildTableFromProcesses(List<BPMNProcess> processes) {
        buildTableHeader();
        buildTableBody(processes);
        buildTableFooter();
    }

    private void buildTableHeader() {
        println("\\begin{center}");
        println("\\begin{tiny}");
        println("\\begin{longtable}{p{0.2\\textwidth}|p{0.8\\textwidth}}");
        println("\\caption{\\ac{BPMN} Conformance Test Cases}" + TABLE_NEWLINE);
        println("\\label{tab:bpmn-suite}" + TABLE_NEWLINE);
        println("\\textbf{Property} & \\textbf{Conformance Test}");
        println("\\vspace{2pt}" + TABLE_NEWLINE);
        println("\\toprule");
        println("\\endfirsthead");
        println("\\multicolumn{2}{c}");
        println("{\\tablename\\ \\thetable\\ -- \\emph{Continued from previous page}}" + TABLE_NEWLINE);
        println("\\endhead");
        println("\\multicolumn{2}{r}{\\emph{Continued on next page}}");
        println("\\endfoot");
        println("\\bottomrule");
        println("\\endlastfoot");
    }

    private void buildTableBody(List<BPMNProcess> processes) {
        for (int i = 0; i < processes.size(); i++) {
            buildTestCases(processes.get(i));

            // print a midrule expect for last assertion
            if (i != processes.size() - 1) {
                println("\\midrule");
            }
        }
    }

    private void buildTestCases(BPMNProcess process) {
        println("Name & " + process.getName().replace("_","-") + COLOUR_NEXT_ROW);
        println("Description & " + process.getDescription() + TABLE_NEWLINE);

        List<BPMNTestCase> cases = process.getTestCases();

        for (int i = 0; i < cases.size(); i++) {
            BPMNTestCase testCase = cases.get(i);

            if (i % 2 != 0 || i == cases.size() - 1) {
                println("Test case " + testCase.getNumber() + " & " + getTestCaseTable(testCase));
            } else {
                println("Test case " + testCase.getNumber() + " & " + getTestCaseTable(testCase) + ROW_COLOUR);
            }
        }
    }

    private String getTestCaseTable(BPMNTestCase testCase) {
        StringBuilder result = new StringBuilder("\\begin{tabular}{cc}");
        result.append("input & trace " + TABLE_NEWLINE + "\\midrule ");

        BPMNTestStep testStep = testCase.getTestStep();

        Optional<BPMNTestVariable> variable = testStep.getVariable();
        if (variable.isPresent()) {
            result.append(variable.get().getValue());
        }

        result.append(" & " + getAssertionsString(testStep.getAssertions()) + TABLE_NEWLINE);

        result.append("\\end{tabular}" + TABLE_NEWLINE);
        return result.toString();
    }

    private String getAssertionsString(List<TestAssertion> assertions) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < assertions.size(); i++) {

            if (i > 0 && i < assertions.size()) {
                result.append(", ");
            }

            TestAssertion testAssertion = assertions.get(i);
            if (testAssertion instanceof BPMNTestAssertion) {
                BPMNTestAssertion bpmnAssertion = (BPMNTestAssertion) testAssertion;
                result.append(bpmnAssertion.getAssertion().toString().replace("SCRIPT_","").replace("_","-"));
            }
        }

        return result.toString();
    }

    private void buildTableFooter() {
        println("\\end{longtable}");
        println("\\end{tiny}");
        println("\\end{center}");
    }

    private void println(String line) {
        System.out.println(line);
    }
}

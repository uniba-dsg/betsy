package betsy.executables.analytics

import betsy.data.Process
import betsy.data.TestCase
import betsy.data.TestStep
import betsy.data.assertions.NotDeployableAssertion
import configuration.processes.Processes
import betsy.data.TestAssertion
import betsy.data.assertions.SoapFaultTestAssertion
import betsy.data.assertions.XpathTestAssertion
import betsy.data.assertions.ExitAssertion

class BpelAnalyzer {

    private File startDir

    private String EXTENSION = "bpel"

    private PrintWriter writer

    private int tableCounter = 0

    private final String[] commonNodes = ["process", "partnerLinks", "partnerLink", "variables", "import", "variable"]

    /**
     * Nodes that are present anyway, given more important nodes are there
     */
    private final String[] minorNodes = ["copy", "from", "to", "for", "until", "literal", "toPart", "messageExchange",
            "fromPart", "correlation", "correlations", "correlationSet", "condition", "extension", "ex:foo", "addr:Address",
            "foo:barEPR", "documentation", "finalCounterValue", "startCounterValue", "branches", "source", "sources", "target",
            "targets", "link"]

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: PATH")
        }

        BpelAnalyzer analyzer = new BpelAnalyzer(new File(args[0]),System.out)
        analyzer.analyzeFilesAndPrintLatex()
    }

    public BpelAnalyzer(File startDir, PrintStream output){
        this.startDir = startDir
        writer = new PrintWriter(output)
    }

    public void analyzeFilesAndPrintLatex() {
        startDir.eachFileRecurse { file ->
            if (file.isFile() && file.name.endsWith(EXTENSION)) {
                printLatexTable(file)
            }
        }
    }



    public void printLatexTableWithResult(String name, String resultString) {
        Process process = getProcess(name)

        File bpelFile = new File(process.getBpelFilePath())

        printTableHeader()

        printTableBody(bpelFile, process)

        printResultSection(resultString)

        printTableFooter(process)
    }

    private void printResultSection(String resultString) {
        write "Support & "

        write "\\begin{tabular}[t]{|p{1,5cm}|p{1,5cm}|p{1,5cm}|p{1,5cm}|p{1,5cm}|}"
        write "\\hline"
        write "bpel-g & ODE & openESB & Orchestra & PetalsESB \\\\\\hline"

        write "${resultString} \\\\\\hline"

        write "\\end{tabular}"

        write "\\\\\\hline"
    }


    private void printLatexTable(File bpelFile) {
        Process process = getProcess(bpelFile.name)

        printTableHeader()

        printTableBody(bpelFile, process)

        printTableFooter(process)
    }

    private printTableHeader() {
        write "\\begin{table}[htbp]"
        write "\\footnotesize"
        write "\\begin{longtable}{| p{5cm} | p{11,5cm} |}"
        write "\\hline"
    }

    private printTableBody(File file, Process process) {
        String name = process.getBpelFileNameWithoutExtension()
        write "Process name & ${name}\\\\\\hline"

        write "Activities and configuration & ${getUncommonNames(file)}\\\\\\hline"

        write "Description & ${process.description}\\\\\\hline"

        process.testCases.each {
            printTestCase(it)
        }

    }

    private void printTestCase(TestCase testCase) {
        write "Test case: ${testCase.name} & "

        write "\\begin{tabular}[t]{|p{1cm}|p{2cm}|p{7cm}|}"
        write "\\hline"
        write "input & operation & assertions \\\\\\hline"
        testCase.testSteps.each {
            printTestStep(it)
        }
        write "\\end{tabular}"

        write "\\\\\\hline"
    }

    private void printTestStep(TestStep step) {
        write "${step.input} & ${getOperationType(step.oneWay)} & ${getAssertionsDescription(step.assertions)} \\\\\\hline"
        if(step.timeToWaitAfterwards != null){
            write "wait for & ${step.timeToWaitAfterwards} ms & \\\\\\hline"
        }
    }

    private String getOperationType(boolean isOneWay){
        if(isOneWay){
            return "asynchronous"
        }
        "synchronous"
    }

    private String getAssertionsDescription(List<TestAssertion> assertions) {
        assertions.collect {
            if (it instanceof NotDeployableAssertion) {
                "NotDeployable"
            } else if (it instanceof SoapFaultTestAssertion){
                SoapFaultTestAssertion assertion = (SoapFaultTestAssertion) it
                "fault: ${assertion.faultString}"
            } else if (it instanceof  XpathTestAssertion) {
                XpathTestAssertion assertion = (XpathTestAssertion) it
                "output: ${assertion.output}"
            } else if (it instanceof  ExitAssertion) {
                "Exit"
            } else {
                throw new IllegalStateException("given assertion did not match")
            }
        }.join("; ")
    }

    private printTableFooter(Process process) {
        String name = process.getBpelFileNameWithoutExtension()
        write "\\end{longtable}"
        write "\\caption{${name} Test}"
        write "\\label{test:${name}}"
        write "\\end{table}"
        write "\\setcounter{table}{\\value{table}-1}"
        write ""

        tableCounter++
        if (tableCounter % 18 == 0) {
            write "\\clearpage"
        }
    }

    private String getUncommonNames(File file) {
        def nodes = getUncommonNodes(file)
        String result = ""
        nodes.each {each ->
            result += "${each} "
        }

        result
    }

    private Process getProcess(String name) {
        Processes processes = new Processes()
        List<Process> all = processes.ALL
        all.find {it.bpelFileName == name }
    }

    private void write(String line) {
        writer.println(line)
        writer.flush()
    }

    /**
     * Computes a set of all node names
     *
     * @return a list of all node names
     */
    SortedSet<String> getNodes(File file) {
        def xml = new XmlSlurper(false, false).parse(file)

        xml.depthFirst().collect { it.name().trim() }.unique().sort() as SortedSet
    }

    SortedSet<String> getUncommonNodes(File file) {
        def nodes = getNodes(file)
        nodes.removeAll(minorNodes)
        nodes.removeAll(commonNodes)

        nodes
    }

}
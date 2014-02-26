package configuration

import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.tasks.FileTasks
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ErrorProcesses {

    public static final Map<String, String> inputToErrorCode = [
            "11001" : "content-empty",
            "12001" : "content-simple-text",
            "12002" : "content-simple-stackTrace",
            "13101" : "content-xml-notWellFormed-twoRootElements",
            "13102" : "content-xml-notWellFormed-overlappingElements",
            "13111" : "content-xml-notWellFormed-unclosed-attribute",
            "13112" : "content-xml-notWellFormed-unclosed-element",
            "13113" : "content-xml-notWellFormed-unclosed-comment",
            "13114" : "content-xml-notWellFormed-unclosed-CDATA",
            "13121" : "content-xml-notWellFormed-unescapedSymbols-lesserThan",
            "13122" : "content-xml-notWellFormed-unescapedSymbols-greaterThan",
            "13123" : "content-xml-notWellFormed-unescapedSymbols-ampersand",
            "13124" : "content-xml-notWellFormed-unescapedSymbols-apostrophy",
            "13125" : "content-xml-notWellFormed-unescapedSymbols-quotation",
            "13131" : "content-xml-notWellFormed-badNames-startsWithXML",
            "13132" : "content-xml-notWellFormed-badNames-startsWithNumber",
            "13133" : "content-xml-notWellFormed-badNames-startsWithPunctuation",
            "13134" : "content-xml-notWellFormed-badNames-startsWithDash",
            "13135" : "content-xml-notWellFormed-badNames-containsSpace",
            "13211" : "content-xml-wrongFormat-missingContent-element",
            "13212" : "content-xml-wrongFormat-missingContent-content",
            "13221" : "content-xml-wrongFormat-dataFormats-stringInstreadOfInteger",
            "13222" : "content-xml-wrongFormat-dataFormats-doubleInsteadOfInteger",
            "13223" : "content-xml-wrongFormat-dataFormats-doubleWithCommaInsteadOfInteger",
            "13224" : "content-xml-wrongFormat-dataFormats-CDATA",
            "13231" : "content-xml-wrongFormat-namespace-noNamespace",
            "13233" : "content-xml-wrongFormat-namespace-wrongNamespace",
            "13234" : "content-xml-wrongFormat-namespace-unusedPrefix",
            "13241" : "content-xml-wrongFormat-additionalContent-element",
            "13242" : "content-xml-wrongFormat-additionalContent-attribute",
            "13243" : "content-xml-wrongFormat-additionalContent-textInElementDefinition",
            "13244" : "content-xml-wrongFormat-additionalContent-textBetweenElements",
            "13301" : "content-xml-wrongSoapStructure-noBody",
            "13302" : "content-xml-wrongSoapStructure-wrongNamespace",
            "23101" : "header-correctMessage-mimeType-application-json",
            "23102" : "header-correctMessage-mimeType-image-svg-xml",
            "23103" : "header-correctMessage-mimeType-multipart-form-data",
            "23104" : "header-correctMessage-mimeType-text-java",
            "23105" : "header-correctMessage-mimeType-test-plain",
    ]

    public static List<BetsyProcess> getProcesses() {

        Path errorsDir = Paths.get("src/main/tests/files/errors")
        FileTasks.deleteDirectory(errorsDir)
        FileTasks.mkdirs(errorsDir)

        List<BetsyProcess> result = new LinkedList<>(); ;

        result.addAll(createTestsForCatchAll(errorsDir))
        result.addAll(createTestsForCatchAllInvokeValidate(errorsDir))

        result.sort() // make sure the happy path is the first test
    }

    private
    static BetsyProcess cloneErrorBetsyProcess(BetsyProcess baseProcess, int number, String name, Path errorsDir) {
        BetsyProcess process = (BetsyProcess) baseProcess.clone()

        // copy file
        String filename = "${baseProcess.getShortId()}_ERR${number}_${name}"
        Path newPath = errorsDir.resolve("${filename}.bpel")
        Files.copy(process.bpel, newPath)

        // update filename
        GPathResult root = new XmlSlurper(false, false).parse(process.bpelFilePath.toFile())
        root.@name = filename
        root.@targetNamespace = filename
        writeXmlFile(newPath, root)

        process.bpel = newPath
        process
    }

    private static List<BetsyProcess> createTestsForCatchAll(Path errorsDir) {
        BetsyProcess baseProcess = ScopeProcesses.SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE

        List<BetsyProcess> result = new LinkedList<>();

        BetsyProcess happyPathProcess = cloneErrorBetsyProcess(baseProcess, 0, "happy-path", errorsDir)
        happyPathProcess.testCases = [new TestCase().checkDeployment().sendSync(0, 0)]
        result.add(happyPathProcess)

        for (Map.Entry<String, String> entry : inputToErrorCode) {

            int number = Integer.parseInt(entry.getKey())
            String name = entry.getValue()
            BetsyProcess process = cloneErrorBetsyProcess(baseProcess, number, name, errorsDir)
            process.testCases = [new TestCase().checkDeployment().sendSync(number, -1)]

            result.add(process)
        }

        result
    }

    private static List<BetsyProcess> createTestsForCatchAllInvokeValidate(Path errorsDir) {
        BetsyProcess baseProcess = ScopeProcesses.SCOPE_FAULT_HANDLERS_CATCH_ALL_INVOKE_VALIDATE

        List<BetsyProcess> result = new LinkedList<>();

        BetsyProcess happyPathProcess = cloneErrorBetsyProcess(baseProcess, 0, "happy-path", errorsDir)
        happyPathProcess.testCases = [new TestCase().checkDeployment().sendSync(0, 0)]
        result.add(happyPathProcess)

        for (Map.Entry<String, String> entry : inputToErrorCode) {

            int number = Integer.parseInt(entry.getKey())
            String name = entry.getValue()
            BetsyProcess process = cloneErrorBetsyProcess(baseProcess, number, name, errorsDir)
            process.testCases = [new TestCase().checkDeployment().sendSync(number, -1)]

            result.add(process)
        }

        result
    }

    private static void writeXmlFile(Path path, GPathResult root) {
        path.toFile().withPrintWriter { out ->
            out.print(XmlUtil.serialize(root))
        }
    }
}

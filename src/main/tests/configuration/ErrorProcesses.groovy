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
            "22042" : "header-correctMessage-statusCode-042",
            "22100" : "header-correctMessage-statusCode-100",
            "22101" : "header-correctMessage-statusCode-101",
            "22102" : "header-correctMessage-statusCode-102",
            "22201" : "header-correctMessage-statusCode-201",
            "22202" : "header-correctMessage-statusCode-202",
            "22203" : "header-correctMessage-statusCode-203",
            "22204" : "header-correctMessage-statusCode-204",
            "22205" : "header-correctMessage-statusCode-205",
            "22206" : "header-correctMessage-statusCode-206",
            "22207" : "header-correctMessage-statusCode-207",
            "22300" : "header-correctMessage-statusCode-300",
            "22301" : "header-correctMessage-statusCode-301",
            "22302" : "header-correctMessage-statusCode-302",
            "22303" : "header-correctMessage-statusCode-303",
            "22304" : "header-correctMessage-statusCode-304",
            "22305" : "header-correctMessage-statusCode-305",
            "22306" : "header-correctMessage-statusCode-306",
            "22307" : "header-correctMessage-statusCode-307",
            "22308" : "header-correctMessage-statusCode-308",
            "22400" : "header-correctMessage-statusCode-400",
            "22401" : "header-correctMessage-statusCode-401",
            "22402" : "header-correctMessage-statusCode-402",
            "22403" : "header-correctMessage-statusCode-403",
            "22404" : "header-correctMessage-statusCode-404",
            "22405" : "header-correctMessage-statusCode-405",
            "22406" : "header-correctMessage-statusCode-406",
            "22407" : "header-correctMessage-statusCode-407",
            "22408" : "header-correctMessage-statusCode-408",
            "22409" : "header-correctMessage-statusCode-409",
            "22410" : "header-correctMessage-statusCode-410",
            "22411" : "header-correctMessage-statusCode-411",
            "22412" : "header-correctMessage-statusCode-412",
            "22413" : "header-correctMessage-statusCode-413",
            "22414" : "header-correctMessage-statusCode-414",
            "22415" : "header-correctMessage-statusCode-415",
            "22416" : "header-correctMessage-statusCode-416",
            "22417" : "header-correctMessage-statusCode-417",
            "22418" : "header-correctMessage-statusCode-418",
            "22500" : "header-correctMessage-statusCode-500",
            "22501" : "header-correctMessage-statusCode-501",
            "22502" : "header-correctMessage-statusCode-502",
            "22503" : "header-correctMessage-statusCode-503",
            "22504" : "header-correctMessage-statusCode-504",
            "22505" : "header-correctMessage-statusCode-505",
            "22506" : "header-correctMessage-statusCode-506",
            "22601" : "header-correctMessage-statusCode-601",
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

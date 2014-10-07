package configuration.bpel

import betsy.bpel.model.BetsyProcess
import betsy.common.model.TestCase
import betsy.common.tasks.FileTasks
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlUtil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ErrorProcesses {

    public static final Map<String, String> inputToErrorCode = [
            "40001" : "custom-xsd-rm-elem",
            "40002" : "custom-xsd-rm-content",
            "40003" : "custom-xsd-mod_content-int_to_string",
            "40004" : "custom-xsd-mod_content-int_to_double",
            "40005" : "custom-xsd-mod_content-int_to_localized_double",
            "40006" : "custom-xsd-mod_content-wrap_with_CDATA",
            "40007" : "custom-xsd-mod_ns-rm_ns",
            "40008" : "custom-xsd-mod_ns-wrong_ns",
            "40009" : "custom-xsd-mod_ns-unbound_prefix",
            "40010" : "custom-xsd-add-element",
            "40011" : "custom-xsd-add-attribute",
            "40012" : "custom-xsd-add-text_between_elems",
            "60001" : "soap-xml-root_elem-none",
            "60002" : "soap-xml-root_elem-text_only",
            "60003" : "soap-xml-root_elem-two_root_elems",
            "60004" : "soap-xml-struct-elems_overlap",
            "60005" : "soap-xml-unclosed-attr",
            "60006" : "soap-xml-unclosed-elem",
            "60007" : "soap-xml-unclosed-comment",
            "60008" : "soap-xml-unclosed-CDATA",
            "60009" : "soap-xml-unesc_sym-lesser_than",
            "60010" : "soap-xml-unesc_sym-greater_than",
            "60011" : "soap-xml-unesc_sym-ampersand",
            "60012" : "soap-xml-unesc_sym-apostrophy",
            "60013" : "soap-xml-unesc_sym-quotation",
            "60014" : "soap-xml-bad_name-starts_with_XML",
            "60015" : "soap-xml-bad_name-starts_with_num",
            "60016" : "soap-xml-bad_name-starts_with_dash",
            "60017" : "soap-xml-bad_name-contains_space",
            "60018" : "soap-xsd-rm-elem",
            "60024" : "soap-xsd-mod_ns-rm_ns",
            "60025" : "soap-xsd-mod_ns-wrong_ns",
            "60026" : "soap-xsd-mod_ns-unbound_prefix",
            "50003" : "tcp-request_timeout"
    ]

    public static List<BetsyProcess> getProcesses() {

        Path errorsDir = Paths.get("src/main/tests/files/bpel/errors")
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

        // update fileName
        GPathResult root = new XmlSlurper(false, false).parse(process.bpelFilePath.toFile())
        root.@name = filename
        root.@targetNamespace = filename
        writeXmlFile(newPath, root)

        process.bpel = newPath
        process
    }

    public static final BetsyProcess BACKDOOR_ROBUSTNESS = new ProcessBuilder().buildProcessWithPartner(
            "errorsbase/BackdoorRobustness", "A receive followed by a scope with fault handlers and an invoke activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(ProcessBuilder.DECLARED_FAULT_CODE, -1)
            ]
    )

    public static final BetsyProcess IMPROVED_BACKDOOR_ROBUSTNESS = new ProcessBuilder().buildProcessWithPartner(
            // only used for error processes. but may also be used as a test
            "errorsbase/ImprovedBackdoorRobustness", "A receive followed by a scope with fault handlers and an invoke as well as a validate activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            [
                    new TestCase().checkDeployment().sendSync(ProcessBuilder.DECLARED_FAULT_CODE, -1)
            ]
    )

    private static List<BetsyProcess> createTestsForCatchAll(Path errorsDir) {
        BetsyProcess baseProcess = BACKDOOR_ROBUSTNESS

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
        BetsyProcess baseProcess = IMPROVED_BACKDOOR_ROBUSTNESS

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

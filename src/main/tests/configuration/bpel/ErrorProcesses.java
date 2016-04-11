package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.tasks.FileTasks;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ErrorProcesses {

    private static final Path ERRORS_DIR = Paths.get("src/main/tests/files/bpel/errors");

    public static List<BPELProcess> createProcesses() {
        FileTasks.deleteDirectory(ERRORS_DIR);
        FileTasks.mkdirs(ERRORS_DIR);

        List<BPELProcess> result = getProcesses();

        for(BPELProcess process : result) {
            // update fileName
            if(process.getProcessFileName().startsWith("IBR_")) {
                XMLTasks.updatesNameAndNamespaceOfRootElement(IMPROVED_BACKDOOR_ROBUSTNESS.getProcess(), process.getProcess(), process.getProcessFileName());
            } else if(process.getProcessFileName().startsWith("BR_")){
                XMLTasks.updatesNameAndNamespaceOfRootElement(BACKDOOR_ROBUSTNESS.getProcess(), process.getProcess(), process.getProcessFileName());
            }
        }

        return result;// make sure the happy path is the first test
    }

    public static void main(String... args) {
        createProcesses(); // this is to recreate the error processes
    }

    public static List<BPELProcess> getProcesses() {
        Path errorsDir = Paths.get("src/main/tests/files/bpel/errors");

        List<BPELProcess> result = new LinkedList<>();

        result.addAll(createTestsForCatchAll(errorsDir));
        result.addAll(createTestsForCatchAllInvokeValidate(errorsDir));

        Collections.sort(result);

        return result;// make sure the happy path is the first test
    }

    private static BPELProcess cloneErrorBetsyProcess(final BPELProcess baseProcess, final int number, final String name, Path errorsDir) {
        BPELProcess result = baseProcess.createCopyWithoutEngine();

        // copy file
        final String filename = baseProcess.getShortId() + "_ERR" + String.valueOf(number) + "_" + name;
        Path newPath = errorsDir.resolve(filename + ".bpel");

        result.setProcess(newPath);

        return result;
    }

    private static List<BPELProcess> createTestsForCatchAll(Path errorsDir) {
        List<BPELProcess> result = new LinkedList<>();

        BPELProcess happyPathProcess = cloneErrorBetsyProcess(BACKDOOR_ROBUSTNESS, 0, "happy-path", errorsDir);
        happyPathProcess.setTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(0, 0))));
        result.add(happyPathProcess);

        for (Map.Entry<String, String> entry : getInputToErrorCode().entrySet()) {
            int number = Integer.parseInt(entry.getKey());
            String name = entry.getValue();
            BPELProcess process = cloneErrorBetsyProcess(BACKDOOR_ROBUSTNESS, number, name, errorsDir);
            process.setTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(number, -1))));

            result.add(process);
        }

        return result;
    }

    private static List<BPELProcess> createTestsForCatchAllInvokeValidate(Path errorsDir) {
        List<BPELProcess> result = new LinkedList<>();

        BPELProcess happyPathProcess = cloneErrorBetsyProcess(IMPROVED_BACKDOOR_ROBUSTNESS, 0, "happy-path", errorsDir);
        happyPathProcess.setTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(0, 0))));
        result.add(happyPathProcess);

        for (Map.Entry<String, String> entry : getInputToErrorCode().entrySet()) {
            int number = Integer.parseInt(entry.getKey());
            String name = entry.getValue();
            BPELProcess process = cloneErrorBetsyProcess(IMPROVED_BACKDOOR_ROBUSTNESS, number, name, errorsDir);
            process.setTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(number, -1))));

            result.add(process);
        }

        return result;
    }

    public static Map<String, String> getInputToErrorCode() {
        Map<String, String> map = new LinkedHashMap<>(34);
        map.put("40001", "custom-xsd-rm-elem");
        map.put("40002", "custom-xsd-rm-content");
        map.put("40003", "custom-xsd-mod_content-int_to_string");
        map.put("40004", "custom-xsd-mod_content-int_to_double");
        map.put("40005", "custom-xsd-mod_content-int_to_localized_double");
        map.put("40006", "custom-xsd-mod_content-wrap_with_CDATA");
        map.put("40007", "custom-xsd-mod_ns-rm_ns");
        map.put("40008", "custom-xsd-mod_ns-wrong_ns");
        map.put("40009", "custom-xsd-mod_ns-unbound_prefix");
        map.put("40010", "custom-xsd-add-element");
        map.put("40011", "custom-xsd-add-attribute");
        map.put("40012", "custom-xsd-add-text_between_elems");
        map.put("60001", "soap-xml-root_elem-none");
        map.put("60002", "soap-xml-root_elem-text_only");
        map.put("60003", "soap-xml-root_elem-two_root_elems");
        map.put("60004", "soap-xml-struct-elems_overlap");
        map.put("60005", "soap-xml-unclosed-attr");
        map.put("60006", "soap-xml-unclosed-elem");
        map.put("60007", "soap-xml-unclosed-comment");
        map.put("60008", "soap-xml-unclosed-CDATA");
        map.put("60009", "soap-xml-unesc_sym-lesser_than");
        map.put("60010", "soap-xml-unesc_sym-greater_than");
        map.put("60011", "soap-xml-unesc_sym-ampersand");
        map.put("60012", "soap-xml-unesc_sym-apostrophy");
        map.put("60013", "soap-xml-unesc_sym-quotation");
        map.put("60014", "soap-xml-bad_name-starts_with_XML");
        map.put("60015", "soap-xml-bad_name-starts_with_num");
        map.put("60016", "soap-xml-bad_name-starts_with_dash");
        map.put("60017", "soap-xml-bad_name-contains_space");
        map.put("60018", "soap-xsd-rm-elem");
        map.put("60024", "soap-xsd-mod_ns-rm_ns");
        map.put("60025", "soap-xsd-mod_ns-wrong_ns");
        map.put("60026", "soap-xsd-mod_ns-unbound_prefix");
        map.put("50003", "tcp-request_timeout");

        return map;
    }

    public static final BPELProcess BACKDOOR_ROBUSTNESS = BPELProcessBuilder.buildErrorProcessWithPartner(
            "errorsbase/BackdoorRobustness",
            "A receive followed by a scope with fault handlers and an invoke activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1));
    public static final BPELProcess IMPROVED_BACKDOOR_ROBUSTNESS = BPELProcessBuilder.buildErrorProcessWithPartner(
            "errorsbase/ImprovedBackdoorRobustness",
            "A receive followed by a scope with fault handlers and an invoke as well as a validate activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1));
}

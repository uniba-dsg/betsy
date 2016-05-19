package configuration.bpel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import betsy.bpel.model.BPELIdShortener;
import betsy.bpel.model.BPELTestCase;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.input.ExternalWSDLTestPartner;
import betsy.common.model.input.NoTestPartner;
import betsy.common.model.input.TestPartner;
import betsy.common.model.input.InternalWSDLTestPartner;
import betsy.common.tasks.FileTasks;

public class ErrorProcesses {

    public static final TestPartner ERROR_TEST_PARTNER = createErrorTestPartner("http://localhost:2000/bpel-testpartner");

    // TODO 50_002 and 50_003 should be better handled
    // TODO better naming according to the feature-tree for these tests

    private static final Path ERRORS_DIR = Paths.get("src/main/tests/files/bpel/errors");

    private static final EngineIndependentProcess BACKDOOR_ROBUSTNESS = BPELProcessBuilder.buildErrorProcessWithPartner(
            "BackdoorRobustness",
            "errorsbase/BackdoorRobustness",
            "A receive followed by a scope with fault handlers and an invoke activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1));
    private static final EngineIndependentProcess IMPROVED_BACKDOOR_ROBUSTNESS = BPELProcessBuilder.buildErrorProcessWithPartner(
            "ImprovedBackdoorRobustness",
            "errorsbase/ImprovedBackdoorRobustness",
            "A receive followed by a scope with fault handlers and an invoke as well as a validate activity. The fault from the invoke activity from the partner service is caught by the scope-level catchAll faultHandler. Inside this faultHandler is the reply to the initial receive.",
            new BPELTestCase().checkDeployment().sendSync(BPELProcessBuilder.DECLARED_FAULT, -1));

    private static List<EngineIndependentProcess> createProcesses() {
        FileTasks.deleteDirectory(ERRORS_DIR);
        FileTasks.mkdirs(ERRORS_DIR);

        List<EngineIndependentProcess> result = getProcesses();

        for (EngineIndependentProcess process : result) {
            // update fileName
            String processFileName = process.getName();
            if (processFileName.startsWith("IBR_")) {
                XMLTasks.updatesNameAndNamespaceOfRootElement(IMPROVED_BACKDOOR_ROBUSTNESS.getProcess(), process.getProcess(), processFileName);
            } else if (processFileName.startsWith("BR_")) {
                XMLTasks.updatesNameAndNamespaceOfRootElement(BACKDOOR_ROBUSTNESS.getProcess(), process.getProcess(), processFileName);
            }
        }

        return result;// make sure the happy path is the first test
    }

    public static void main(String... args) {
        createProcesses(); // this is to recreate the error processes
    }

    public static List<EngineIndependentProcess> getProcesses() {
        Path errorsDir = Paths.get("src/main/tests/files/bpel/errors");

        List<EngineIndependentProcess> result = new LinkedList<>();

        result.addAll(createTests(errorsDir, BACKDOOR_ROBUSTNESS));
        result.addAll(createTests(errorsDir, IMPROVED_BACKDOOR_ROBUSTNESS));

        Collections.sort(result);

        return result;// make sure the happy path is the first test
    }

    private static EngineIndependentProcess cloneErrorBetsyProcess(final EngineIndependentProcess baseProcess, final int number, final Feature feature, Path errorsDir) {
        // copy file
        String shortenedId = new BPELIdShortener(baseProcess.getName()).getShortenedId();
        final String filename = shortenedId + "_ERR" + String.valueOf(number) + "_" + feature.getName();
        Path newPath = errorsDir.resolve(filename + ".bpel");

        return baseProcess.withNewProcessAndFeature(newPath, new Feature(feature.construct, FileTasks.getFilenameWithoutExtension(newPath.getFileName().toString())));
    }

    private static List<EngineIndependentProcess> createTests(Path errorsDir, EngineIndependentProcess baseProcess) {
        List<EngineIndependentProcess> result = new LinkedList<>();

        EngineIndependentProcess happyPathProcess = cloneErrorBetsyProcess(baseProcess, 0, new Feature(APP_CONSTRUCT, "happy-path"), errorsDir);
        happyPathProcess = happyPathProcess.withNewTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(0, 0))));
        result.add(happyPathProcess);

        for (Error error : getInputToErrorCode()) {
            int number = error.number;
            Feature feature = new Feature(error.construct, error.name);
            EngineIndependentProcess process = cloneErrorBetsyProcess(baseProcess, number, feature, errorsDir);
            process = process.withNewTestCases(new ArrayList<>(Collections.singletonList(new BPELTestCase().checkDeployment().sendSync(number, -1))));

            result.add(process);
        }

        return result;
    }

    private static final Construct HTTP_CONSTRUCT = new Construct(Groups.ERROR, "http");
    private static final Construct SOAP_CONSTRUCT = new Construct(Groups.ERROR, "soap");
    private static final Construct TCP_CONSTRUCT = new Construct(Groups.ERROR, "tcp");
    private static final Construct APP_CONSTRUCT = new Construct(Groups.ERROR, "app");

    static TestPartner createErrorTestPartner(String url) {
        List<InternalWSDLTestPartner.OperationInputOutputRule> tcpActions = new ArrayList<>();
        tcpActions.add(new InternalWSDLTestPartner.OperationInputOutputRule(
                        "startProcessSync",
                        new InternalWSDLTestPartner.IntegerInput(50_001),
                        new InternalWSDLTestPartner.TimeoutInsteadOfOutput()));

        List<InternalWSDLTestPartner.OperationInputOutputRule> httpActions = IntStream.of(
                100, 101,
                201, 202, 203, 204, 205, 206,
                300, 301, 302, 303, 304, 305, 306, 307,
                400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417,
                500, 501, 502, 503, 504, 505).mapToObj(i -> new InternalWSDLTestPartner.OperationInputOutputRule(
                        "startProcessSync",
                        new InternalWSDLTestPartner.IntegerInput(i + 22_000),
                        new InternalWSDLTestPartner.IntegerOutputWithStatusCode(0, i)
                )
        ).collect(Collectors.toList());

        List<InternalWSDLTestPartner.OperationInputOutputRule> soapActions = IntStream.range(60_001, 60_027).mapToObj(i -> {
            String rawOutput = "";
            try {
                Path folder = Paths.get("src/tests/files/bpel/errrorsbase/soap");
                Optional<Path> foundXmlFile = Files.walk(folder).filter(f -> f.getFileName().toString().startsWith(String.valueOf(i))).findFirst();
                if(foundXmlFile.isPresent()) {
                    rawOutput = String.join("\n", Files.readAllLines(foundXmlFile.get()));
                }
            } catch (IOException e) {
            }

            return new InternalWSDLTestPartner.OperationInputOutputRule(
                    "startProcessSync",
                    new InternalWSDLTestPartner.IntegerInput(i),
                    new InternalWSDLTestPartner.RawOutput(rawOutput));
        }).collect(Collectors.toList());

        List<InternalWSDLTestPartner.OperationInputOutputRule> appActions = IntStream.range(40_001, 40_027).mapToObj(i -> {
            String rawOutput = "";
            try {
                Path folder = Paths.get("src/tests/files/bpel/errrorsbase/app");
                Optional<Path> foundXmlFile = Files.walk(folder).filter(f -> f.getFileName().toString().startsWith(String.valueOf(i))).findFirst();
                if(foundXmlFile.isPresent()) {
                    rawOutput = String.join("\n", Files.readAllLines(foundXmlFile.get()));
                }
            } catch (IOException e) {
            }

            return new InternalWSDLTestPartner.OperationInputOutputRule(
                    "startProcessSync",
                    new InternalWSDLTestPartner.IntegerInput(i),
                    new InternalWSDLTestPartner.RawOutput(rawOutput));
        }).collect(Collectors.toList());

        List<InternalWSDLTestPartner.OperationInputOutputRule> actions = new ArrayList<>();
        actions.addAll(httpActions);
        actions.addAll(soapActions);
        actions.addAll(appActions);
        actions.add(new InternalWSDLTestPartner.OperationInputOutputRule("startProcessSync", new InternalWSDLTestPartner.AnyInput(), new InternalWSDLTestPartner.EchoInputAsOutput()));

        return new InternalWSDLTestPartner(
                Paths.get("TestPartner.wsdl"),
                url,
                actions.toArray(new InternalWSDLTestPartner.OperationInputOutputRule[] {})
        );
    }

    private static class Error {
        public final int number;
        public final String name;
        public final Construct construct;
        public final TestPartner testPartner;

        private Error(int number, String name, Construct construct) {
            this.number = number;
            this.name = name;
            this.construct = construct;
            this.testPartner = ErrorProcesses.createErrorTestPartner("http://localhost:2000/bpel-testpartner");
        }

        public Error(int number, String name, Construct construct, TestPartner testPartner) {
            this.number = number;
            this.name = name;
            this.construct = construct;
            this.testPartner = testPartner;
        }
    }

    private static List<Error> getInputToErrorCode() {
        List<Error> result = new LinkedList<>();
        result.add(new Error(40001, "custom-xsd-rm-elem", APP_CONSTRUCT));
        result.add(new Error(40002, "custom-xsd-rm-content", APP_CONSTRUCT));
        result.add(new Error(40003, "custom-xsd-mod_content-int_to_string", APP_CONSTRUCT));
        result.add(new Error(40004, "custom-xsd-mod_content-int_to_double", APP_CONSTRUCT));
        result.add(new Error(40005, "custom-xsd-mod_content-int_to_localized_double", APP_CONSTRUCT));
        result.add(new Error(40006, "custom-xsd-mod_content-wrap_with_CDATA", APP_CONSTRUCT));
        result.add(new Error(40007, "custom-xsd-mod_ns-rm_ns", APP_CONSTRUCT));
        result.add(new Error(40008, "custom-xsd-mod_ns-wrong_ns", APP_CONSTRUCT));
        result.add(new Error(40009, "custom-xsd-mod_ns-unbound_prefix", APP_CONSTRUCT));
        result.add(new Error(40010, "custom-xsd-add-element", APP_CONSTRUCT));
        result.add(new Error(40011, "custom-xsd-add-attribute", APP_CONSTRUCT));
        result.add(new Error(40012, "custom-xsd-add-text_between_elems", APP_CONSTRUCT));

        result.add(new Error(22100, "header-correctMessage-statusCode-100", HTTP_CONSTRUCT));
        result.add(new Error(22101, "header-correctMessage-statusCode-101", HTTP_CONSTRUCT));
        result.add(new Error(22201, "header-correctMessage-statusCode-201", HTTP_CONSTRUCT));
        result.add(new Error(22202, "header-correctMessage-statusCode-202", HTTP_CONSTRUCT));
        result.add(new Error(22203, "header-correctMessage-statusCode-203", HTTP_CONSTRUCT));
        result.add(new Error(22204, "header-correctMessage-statusCode-204", HTTP_CONSTRUCT));
        result.add(new Error(22205, "header-correctMessage-statusCode-205", HTTP_CONSTRUCT));
        result.add(new Error(22206, "header-correctMessage-statusCode-206", HTTP_CONSTRUCT));
        result.add(new Error(22300, "header-correctMessage-statusCode-300", HTTP_CONSTRUCT));
        result.add(new Error(22301, "header-correctMessage-statusCode-301", HTTP_CONSTRUCT));
        result.add(new Error(22302, "header-correctMessage-statusCode-302", HTTP_CONSTRUCT));
        result.add(new Error(22303, "header-correctMessage-statusCode-303", HTTP_CONSTRUCT));
        result.add(new Error(22304, "header-correctMessage-statusCode-304", HTTP_CONSTRUCT));
        result.add(new Error(22305, "header-correctMessage-statusCode-305", HTTP_CONSTRUCT));
        result.add(new Error(22306, "header-correctMessage-statusCode-306", HTTP_CONSTRUCT));
        result.add(new Error(22307, "header-correctMessage-statusCode-307", HTTP_CONSTRUCT));
        result.add(new Error(22400, "header-correctMessage-statusCode-400", HTTP_CONSTRUCT));
        result.add(new Error(22401, "header-correctMessage-statusCode-401", HTTP_CONSTRUCT));
        result.add(new Error(22402, "header-correctMessage-statusCode-402", HTTP_CONSTRUCT));
        result.add(new Error(22403, "header-correctMessage-statusCode-403", HTTP_CONSTRUCT));
        result.add(new Error(22404, "header-correctMessage-statusCode-404", HTTP_CONSTRUCT));
        result.add(new Error(22405, "header-correctMessage-statusCode-405", HTTP_CONSTRUCT));
        result.add(new Error(22406, "header-correctMessage-statusCode-406", HTTP_CONSTRUCT));
        result.add(new Error(22407, "header-correctMessage-statusCode-407", HTTP_CONSTRUCT));
        result.add(new Error(22408, "header-correctMessage-statusCode-408", HTTP_CONSTRUCT));
        result.add(new Error(22409, "header-correctMessage-statusCode-409", HTTP_CONSTRUCT));
        result.add(new Error(22410, "header-correctMessage-statusCode-410", HTTP_CONSTRUCT));
        result.add(new Error(22411, "header-correctMessage-statusCode-411", HTTP_CONSTRUCT));
        result.add(new Error(22412, "header-correctMessage-statusCode-412", HTTP_CONSTRUCT));
        result.add(new Error(22413, "header-correctMessage-statusCode-413", HTTP_CONSTRUCT));
        result.add(new Error(22414, "header-correctMessage-statusCode-414", HTTP_CONSTRUCT));
        result.add(new Error(22415, "header-correctMessage-statusCode-415", HTTP_CONSTRUCT));
        result.add(new Error(22416, "header-correctMessage-statusCode-416", HTTP_CONSTRUCT));
        result.add(new Error(22417, "header-correctMessage-statusCode-417", HTTP_CONSTRUCT));
        result.add(new Error(22500, "header-correctMessage-statusCode-500", HTTP_CONSTRUCT));
        result.add(new Error(22501, "header-correctMessage-statusCode-501", HTTP_CONSTRUCT));
        result.add(new Error(22502, "header-correctMessage-statusCode-502", HTTP_CONSTRUCT));
        result.add(new Error(22503, "header-correctMessage-statusCode-503", HTTP_CONSTRUCT));
        result.add(new Error(22504, "header-correctMessage-statusCode-504", HTTP_CONSTRUCT));
        result.add(new Error(22505, "header-correctMessage-statusCode-505", HTTP_CONSTRUCT));

        result.add(new Error(60001, "soap-xml-root_elem-none", SOAP_CONSTRUCT));
        result.add(new Error(60002, "soap-xml-root_elem-text_only", SOAP_CONSTRUCT));
        result.add(new Error(60003, "soap-xml-root_elem-two_root_elems", SOAP_CONSTRUCT));
        result.add(new Error(60004, "soap-xml-struct-elems_overlap", SOAP_CONSTRUCT));
        result.add(new Error(60005, "soap-xml-unclosed-attr", SOAP_CONSTRUCT));
        result.add(new Error(60006, "soap-xml-unclosed-elem", SOAP_CONSTRUCT));
        result.add(new Error(60007, "soap-xml-unclosed-comment", SOAP_CONSTRUCT));
        result.add(new Error(60008, "soap-xml-unclosed-CDATA", SOAP_CONSTRUCT));
        result.add(new Error(60009, "soap-xml-unesc_sym-lesser_than", SOAP_CONSTRUCT));
        result.add(new Error(60010, "soap-xml-unesc_sym-greater_than", SOAP_CONSTRUCT));
        result.add(new Error(60011, "soap-xml-unesc_sym-ampersand", SOAP_CONSTRUCT));
        result.add(new Error(60012, "soap-xml-unesc_sym-apostrophy", SOAP_CONSTRUCT));
        result.add(new Error(60013, "soap-xml-unesc_sym-quotation", SOAP_CONSTRUCT));
        result.add(new Error(60014, "soap-xml-bad_name-starts_with_XML", SOAP_CONSTRUCT));
        result.add(new Error(60015, "soap-xml-bad_name-starts_with_num", SOAP_CONSTRUCT));
        result.add(new Error(60016, "soap-xml-bad_name-starts_with_dash", SOAP_CONSTRUCT));
        result.add(new Error(60017, "soap-xml-bad_name-contains_space", SOAP_CONSTRUCT));
        result.add(new Error(60018, "soap-xsd-rm-elem", SOAP_CONSTRUCT));
        result.add(new Error(60024, "soap-xsd-mod_ns-rm_ns", SOAP_CONSTRUCT));
        result.add(new Error(60025, "soap-xsd-mod_ns-wrong_ns", SOAP_CONSTRUCT));
        result.add(new Error(60026, "soap-xsd-mod_ns-unbound_prefix", SOAP_CONSTRUCT));
        result.add(new Error(50001, "tcp-dns-unresolveable", TCP_CONSTRUCT, new ExternalWSDLTestPartner("http://thishostisnotusedeverreally:2000/bpel-testpartner", Paths.get("TestPartner.wsdl"))));
        result.add(new Error(50002, "tcp-host-unreachable", TCP_CONSTRUCT, new NoTestPartner()));
        result.add(new Error(50003, "tcp-timeout", TCP_CONSTRUCT, new InternalWSDLTestPartner(Paths.get("TestPartner.wsdl"),
                "http://localhost:2000/bpel-testpartner",
                new InternalWSDLTestPartner.OperationInputOutputRule("startProcessSync", new InternalWSDLTestPartner.AnyInput(), new InternalWSDLTestPartner.TimeoutInsteadOfOutput()))));

        return result;
    }
    
}

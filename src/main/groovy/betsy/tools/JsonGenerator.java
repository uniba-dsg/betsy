package betsy.tools;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.assertions.SoapFaultTestAssertion;
import betsy.bpel.model.assertions.XpathTestAssertion;
import betsy.bpel.model.steps.DelayTestStep;
import betsy.bpel.model.steps.DeployableCheckTestStep;
import betsy.bpel.model.steps.NotDeployableCheckTestStep;
import betsy.bpel.model.steps.SoapTestStep;
import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestStep;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.analytics.additional.BPELTestTreePrinter;
import betsy.common.analytics.additional.BPMNTestTreePrinter;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.*;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class JsonGenerator {

    static class FeatureTreeJsonGenerator {
        private static void generatesConstructsJson() {
            JSONArray featureTree = new JSONArray();

            try {
                addBpmn(featureTree);
                addBpel(featureTree);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("feature-tree.json"), StandardOpenOption.CREATE)) {
                writer.append(featureTree.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void addBpel(JSONArray featureTreeArray) throws IOException {
            JSONObject languageObject = new JSONObject();
            languageObject.put("name", ProcessLanguage.BPEL.name());
            languageObject.put("id", ProcessLanguage.BPEL.getID());
            JSONArray groupsArray = new JSONArray();
            languageObject.put("groups", groupsArray);

            TestNameToLanguageFeature bpel = new TestNameToLanguageFeature(BPELTestTreePrinter.class.getResourceAsStream("BpelLanguageConstructs.properties"));

            BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
            List<EngineIndependentProcess> processes = repository.getByName("ALL");

            Map<String, Map<String, List<EngineIndependentProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(p -> p.getGroup().getName(),
                            Collectors.groupingBy(p -> bpel.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<EngineIndependentProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();

                JSONObject groupObject = new JSONObject();
                groupObject.put("name", group);
                groupObject.put("description", "");
                groupObject.put("id", String.join("__", languageObject.getString("name"), groupObject.getString("name")));
                JSONArray constructsArray = new JSONArray();
                groupObject.put("constructs", constructsArray);

                for(Map.Entry<String, List<EngineIndependentProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("description", "");
                    constructObject.put("id", String.join("__", languageObject.getString("name"), groupObject.getString("name"), constructObject.getString("name")));
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(EngineIndependentProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        groupObject.put("description", process.getGroup().description);

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        String featureID = String.join("__", languageObject.getString("name"), groupObject.getString("name"), constructObject.getString("name"), featureObject.getString("name"));
                        featureObject.put("id", featureID);
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructsArray.put(constructObject);
                }

                groupsArray.put(groupObject);
            }
            featureTreeArray.put(languageObject);
        }

        private static void addBpmn(JSONArray featureTreeArray) throws IOException {
            JSONObject languageObject = new JSONObject();
            languageObject.put("name", ProcessLanguage.BPMN.name());
            languageObject.put("id", ProcessLanguage.BPMN.getID());
            JSONArray groupsArray = new JSONArray();
            languageObject.put("groups", groupsArray);

            TestNameToLanguageFeature bpmn = new TestNameToLanguageFeature(BPMNTestTreePrinter.class.getResourceAsStream("BpmnLanguageConstructs.properties"));
            BPMNProcessRepository repository = new BPMNProcessRepository();
            List<EngineIndependentProcess> processes = repository.getByName("ALL");
            Map<String, Map<String, List<EngineIndependentProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(p -> p.getGroup().getName(),
                            Collectors.groupingBy(p -> bpmn.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<EngineIndependentProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();

                JSONObject groupObject = new JSONObject();
                groupObject.put("name", group);
                groupObject.put("description", "");
                groupObject.put("id", String.join("__", languageObject.getString("name"), groupObject.getString("name")));
                JSONArray constructsArray = new JSONArray();
                groupObject.put("constructs", constructsArray);

                for(Map.Entry<String, List<EngineIndependentProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("id", String.join("__", languageObject.getString("name"), groupObject.getString("name"), constructObject.getString("name")));
                    constructObject.put("description", "");
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(EngineIndependentProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        groupObject.put("description", process.getGroup().description);

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        String featureID = String.join("__", languageObject.getString("name"), groupObject.getString("name"), constructObject.getString("name"), featureObject.getString("name"));
                        featureObject.put("id", featureID);
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructsArray.put(constructObject);
                }

                groupsArray.put(groupObject);
            }
            featureTreeArray.put(languageObject);
        }

        static class TestNameToLanguageFeature {

            private final Properties properties = new Properties();

            public TestNameToLanguageFeature(InputStream stream) throws IOException {
                properties.load(stream);
            }

            public String getGroupByTestName(String testname){
                Object group = properties.get(testname);
                if(group == null){
                    throw new IllegalStateException(testname + " has no language feature");
                }
                return group.toString();
            }
        }
    }

    static class ConstructsJsonGenerator {
        private static void generatesConstructsJson() {
            JSONArray constructArray = new JSONArray();

            try {
                addBpmn(constructArray);
                addBpel(constructArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("constructs.json"), StandardOpenOption.CREATE)) {
                writer.append(constructArray.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void addBpel(JSONArray constructArray) throws IOException {
            TestNameToLanguageFeature bpel = new TestNameToLanguageFeature(BPELTestTreePrinter.class.getResourceAsStream("BpelLanguageConstructs.properties"));

            BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
            List<EngineIndependentProcess> processes = repository.getByName("ALL");

            Map<String, Map<String, List<EngineIndependentProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(p -> p.getGroup().getName(),
                            Collectors.groupingBy(p -> bpel.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<EngineIndependentProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();

                for(Map.Entry<String, List<EngineIndependentProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("description", "");
                    constructObject.put("group", group);
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(EngineIndependentProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        featureObject.put("id", process.getFeature().getID());
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructArray.put(constructObject);
                }
            }
        }

        private static void addBpmn(JSONArray constructArray) throws IOException {
            TestNameToLanguageFeature bpmn = new TestNameToLanguageFeature(BPMNTestTreePrinter.class.getResourceAsStream("BpmnLanguageConstructs.properties"));
            BPMNProcessRepository repository = new BPMNProcessRepository();
            List<EngineIndependentProcess> processes = repository.getByName("ALL");
            Map<String, Map<String, List<EngineIndependentProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(p -> p.getGroup().getName(),
                            Collectors.groupingBy(p -> bpmn.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<EngineIndependentProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();
                for(Map.Entry<String, List<EngineIndependentProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("description", "");
                    constructObject.put("group", group);
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(EngineIndependentProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        featureObject.put("id", process.getFeature().getID());
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructArray.put(constructObject);
                }
            }
        }

        static class TestNameToLanguageFeature {

            private final Properties properties = new Properties();

            public TestNameToLanguageFeature(InputStream stream) throws IOException {
                properties.load(stream);
            }

            public String getGroupByTestName(String testname){
                Object group = properties.get(testname);
                if(group == null){
                    throw new IllegalStateException(testname + " has no language feature");
                }
                return group.toString();
            }
        }
    }

    static class EnginesJsonGenerator {

        private static void generateEnginesJson() {
            JSONArray array = new JSONArray();

            for(EngineLifecycle e : getEngines()) {
                boolean excludeVirtualEngines = !(e instanceof VirtualEngineAPI);
                if(e instanceof IsEngine && excludeVirtualEngines) {
                    Engine engine = ((IsEngine) e).getEngineId();
                    JSONObject object = new JSONObject();
                    object.put("id", engine.getNormalizedId());
                    object.put("name", engine.getName());
                    object.put("version", engine.getVersion());
                    object.put("configuration", engine.getConfiguration());
                    object.put("language", engine.getLanguage().name());
                    array.put(object);
                }
            }

            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("engines.json"), StandardOpenOption.CREATE)) {
                writer.append(array.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static List<EngineLifecycle> getEngines() {
            final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
            final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
            final List<EngineLifecycle> engines = new LinkedList<>();
            engines.addAll(bpelEngines);
            engines.addAll(bpmnEngines);

            return engines;
        }
    }

    static class TestsEngineIndependentJsonGenerator {
        private static void generateTestsEngineIndependentJson() {
            JSONArray array = new JSONArray();

            addBPMNTests(array);
            addBPELTests(array);

            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("tests-engine-independent.json"), StandardOpenOption.CREATE)) {
                writer.append(array.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void addBPELTests(JSONArray array) {
            BPELProcessRepository bpelRepo = BPELProcessRepository.INSTANCE;
            for(EngineIndependentProcess p : bpelRepo.getByName("ALL")) {

                JSONObject testObject = new JSONObject();
                testObject.put("name", p.getName());

                // FIXME description of feature and test is the same, it is not differentiated
                testObject.put("description", p.getDescription());

                JSONArray engineIndependentFiles = new JSONArray();
                testObject.put("engineIndependentFiles", engineIndependentFiles);
                engineIndependentFiles.put(p.getProcess().toString());
                for(Path path : p.getFiles(x -> !x.toString().endsWith(".wsdl"))) {
                    engineIndependentFiles.put(path.toString());
                }
                for(Path path : p.getFiles(x -> x.toString().endsWith(".wsdl"))) {
                    engineIndependentFiles.put(path.toString());
                }

                testObject.put("language", p.getProcessLanguage().name());
                testObject.put("featureID", p.getFeature().getID());

                JSONArray testCasesArray = new JSONArray();
                testObject.put("testCases", testCasesArray);

                for(TestCase testCase : p.getTestCases()) {
                    JSONObject testCaseObject = new JSONObject();

                    testCaseObject.put("number", testCase.getNumber());
                    testCaseObject.put("name", testCase.getName());

                    JSONArray testStepArray = new JSONArray();

                    for(TestStep testStep : testCase.getTestSteps()) {
                        JSONObject testStepObject = new JSONObject();
                        testStepObject.put("description", testStep.getDescription());
                        if(testStep instanceof DelayTestStep) {
                            testStepObject.put("type", testStep.getClass().getSimpleName());
                            testStepObject.put("delay", ((DelayTestStep) testStep).getTimeToWaitAfterwards());
                        } else if(testStep instanceof DeployableCheckTestStep) {
                            JSONArray assertionsArray = new JSONArray();
                            testStepObject.put("assertions", assertionsArray);
                            JSONObject assertionObject = new JSONObject();
                            assertionObject.put("type", "DeployableAssertion");
                            assertionsArray.put(assertionObject);
                        } else if(testStep instanceof NotDeployableCheckTestStep) {
                            JSONArray assertionsArray = new JSONArray();
                            testStepObject.put("assertions", assertionsArray);
                            JSONObject assertionObject = new JSONObject();
                            assertionObject.put("type", "NotDeployableAssertion");
                            assertionsArray.put(assertionObject);
                        } else if(testStep instanceof SoapTestStep) {
                            testStepObject.put("type", testStep.getClass().getSimpleName());
                            testStepObject.put("input", ((SoapTestStep) testStep).getInput());
                            if(((SoapTestStep) testStep).getOperation() != null) {
                                testStepObject.put("operation", ((SoapTestStep) testStep).getOperation().getName());
                            }

                            testStepObject.put("concurrencyTest", ((SoapTestStep) testStep).isConcurrencyTest());
                            testStepObject.put("testPartner", ((SoapTestStep) testStep).isTestPartner());
                            testStepObject.put("oneWay", ((SoapTestStep) testStep).isOneWay());

                            JSONArray assertionsArray = new JSONArray();
                            testStepObject.put("assertions", assertionsArray);
                            for(TestAssertion assertion : ((SoapTestStep) testStep).getAssertions()) {
                                JSONObject assertionObject = new JSONObject();
                                assertionObject.put("type", assertion.getClass().getSimpleName());
                                if(assertion instanceof SoapFaultTestAssertion) {
                                    assertionObject.put("faultString", ((SoapFaultTestAssertion) assertion).getFaultString());
                                } else if(assertion instanceof XpathTestAssertion) {
                                    assertionObject.put("value", ((XpathTestAssertion) assertion).getExpectedOutput());
                                    assertionObject.put("xpathExpression", ((XpathTestAssertion) assertion).getXpathExpression());
                                }
                                assertionsArray.put(assertionObject);
                            }

                        }
                        testStepArray.put(testStepObject);
                    }

                    testCaseObject.put("testSteps", testStepArray);
                    testCasesArray.put(testCaseObject);
                }

                array.put(testObject);
            }
        }

        private static void addBPMNTests(JSONArray array) {
            BPMNProcessRepository bpmnRepo = new BPMNProcessRepository();
            for(EngineIndependentProcess p : bpmnRepo.getByName("ALL")) {

                JSONObject testObject = new JSONObject();
                testObject.put("name", p.getName());

                // FIXME description of feature and test is the same, it is not differentiated
                testObject.put("description", p.getDescription());

                JSONArray engineIndependentFiles = new JSONArray();
                testObject.put("engineIndependentFiles", engineIndependentFiles);
                engineIndependentFiles.put(p.getProcess().toString());

                testObject.put("language", p.getProcessLanguage().name());
                testObject.put("featureID", p.getFeature().getID());

                JSONArray testCasesArray = new JSONArray();
                testObject.put("testCases", testCasesArray);

                for(TestCase testCase : p.getTestCases()) {
                    JSONObject testCaseObject = new JSONObject();

                    testCaseObject.put("number", testCase.getNumber());
                    testCaseObject.put("name", testCase.getName());

                    BPMNTestCase bpmnTestCase = (BPMNTestCase) testCase;
                    BPMNTestStep testStep = bpmnTestCase.getTestStep();
                    JSONObject testStepObject = new JSONObject();
                    testStepObject.put("type", testStep.getClass().getSimpleName());
                    testStepObject.put("description", testStep.getDescription());
                    testStep.getDelay().ifPresent(delay -> testStepObject.put("delay", delay));
                    JSONArray inputArray = new JSONArray();
                    testStepObject.put("inputs", inputArray);
                    for(BPMNTestVariable var : bpmnTestCase.getVariables()) {
                        JSONObject varObject = new JSONObject();
                        varObject.put("name", var.getName());
                        varObject.put("value", var.getValue());
                        varObject.put("type", var.getType());
                        inputArray.put(varObject);
                    }

                    JSONArray assertionsArray = new JSONArray();
                    testStepObject.put("assertions", assertionsArray);
                    for(String assertion : bpmnTestCase.getAssertions()) {
                        JSONObject varObject = new JSONObject();
                        varObject.put("type", "BPMNTestAssertion");
                        varObject.put("trace", assertion);
                        assertionsArray.put(varObject);
                    }

                    JSONArray testStepArray = new JSONArray();
                    testStepArray.put(testStepObject);
                    testCaseObject.put("testSteps", testStepArray);

                    testCasesArray.put(testCaseObject);
                }

                array.put(testObject);
            }
        }
    }

    public static void main(String[] args) {
        EnginesJsonGenerator.generateEnginesJson();
        FeatureTreeJsonGenerator.generatesConstructsJson();
        ConstructsJsonGenerator.generatesConstructsJson();
        TestsEngineIndependentJsonGenerator.generateTestsEngineIndependentJson();
    }

}

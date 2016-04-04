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
            List<BPELProcess> processes = repository.getByName("ALL");

            Map<String, Map<String, List<BPELProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(ProcessFolderStructure::getGroup,
                            Collectors.groupingBy(p -> bpel.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<BPELProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();

                for(Map.Entry<String, List<BPELProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("description", "");
                    constructObject.put("group", group);
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(BPELProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        featureObject.put("id", process.getNormalizedId());
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructArray.put(constructObject);
                }
            }
        }

        private static TestNameToLanguageFeature addBpmn(JSONArray constructArray) throws IOException {
            TestNameToLanguageFeature bpmn = new TestNameToLanguageFeature(BPMNTestTreePrinter.class.getResourceAsStream("BpmnLanguageConstructs.properties"));
            BPMNProcessRepository repository = new BPMNProcessRepository();
            List<BPMNProcess> processes = repository.getByName("ALL");
            Map<String, Map<String, List<BPMNProcess>>> entries = processes.stream().
                    collect(Collectors.groupingBy(ProcessFolderStructure::getGroup,
                            Collectors.groupingBy(p -> bpmn.getGroupByTestName(p.getName()))));
            for(Map.Entry<String, Map<String, List<BPMNProcess>>> entry : entries.entrySet()) {
                String group = entry.getKey();
                for(Map.Entry<String, List<BPMNProcess>> entry2 : entry.getValue().entrySet()) {
                    String construct = entry2.getKey();

                    JSONObject constructObject = new JSONObject();
                    constructObject.put("name", construct);
                    constructObject.put("description", "");
                    constructObject.put("group", group);
                    JSONArray featuresArray = new JSONArray();
                    constructObject.put("features", featuresArray);

                    for(BPMNProcess process : entry2.getValue()) {
                        String feature = process.getName();

                        JSONObject featureObject = new JSONObject();
                        featureObject.put("name", feature);
                        featureObject.put("description", process.getDescription());
                        featureObject.put("id", process.getNormalizedId());
                        featureObject.put("language", process.getProcessLanguage().name());
                        featuresArray.put(featureObject);
                    }

                    constructArray.put(constructObject);
                }
            }
            return bpmn;
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
            for(BPELProcess p : bpelRepo.getByName("ALL")) {

                JSONObject testObject = new JSONObject();
                testObject.put("name", p.getName());

                // FIXME description of feature and test is the same, it is not differentiated
                testObject.put("description", p.getDescription());

                JSONArray engineIndependentFiles = new JSONArray();
                testObject.put("engineIndependentFiles", engineIndependentFiles);
                engineIndependentFiles.put(p.getProcess().toString());
                for(Path path : p.getAdditionalFilePaths()) {
                    engineIndependentFiles.put(path.toString());
                }
                for(Path path : p.getWsdlPaths()) {
                    engineIndependentFiles.put(path.toString());
                }

                testObject.put("language", p.getProcessLanguage().name());
                testObject.put("featureID", p.getNormalizedId());

                JSONArray testCasesArray = new JSONArray();
                testObject.put("testCases", testCasesArray);

                for(BPELTestCase testCase : p.getTestCases()) {
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
            for(BPMNProcess p : bpmnRepo.getByName("ALL")) {

                JSONObject testObject = new JSONObject();
                testObject.put("name", p.getName());

                // FIXME description of feature and test is the same, it is not differentiated
                testObject.put("description", p.getDescription());

                JSONArray engineIndependentFiles = new JSONArray();
                testObject.put("engineIndependentFiles", engineIndependentFiles);
                engineIndependentFiles.put(p.getProcess().toString());

                testObject.put("language", p.getProcessLanguage().name());
                testObject.put("featureID", p.getNormalizedId());

                JSONArray testCasesArray = new JSONArray();
                testObject.put("testCases", testCasesArray);

                for(BPMNTestCase testCase : p.getTestCases()) {
                    JSONObject testCaseObject = new JSONObject();

                    testCaseObject.put("number", testCase.getNumber());
                    testCaseObject.put("name", testCase.getName());

                    BPMNTestStep testStep = testCase.getTestStep();
                    JSONObject testStepObject = new JSONObject();
                    testStepObject.put("type", testStep.getClass().getSimpleName());
                    testStepObject.put("description", testStep.getDescription());
                    testStep.getDelay().ifPresent(delay -> testStepObject.put("delay", delay));
                    JSONArray inputArray = new JSONArray();
                    testStepObject.put("inputs", inputArray);
                    for(BPMNTestVariable var : testCase.getVariables()) {
                        JSONObject varObject = new JSONObject();
                        varObject.put("name", var.getName());
                        varObject.put("value", var.getValue());
                        varObject.put("type", var.getType());
                        inputArray.put(varObject);
                    }

                    JSONArray assertionsArray = new JSONArray();
                    testStepObject.put("assertions", assertionsArray);
                    for(String assertion : testCase.getAssertions()) {
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
        ConstructsJsonGenerator.generatesConstructsJson();
        TestsEngineIndependentJsonGenerator.generateTestsEngineIndependentJson();
    }

}
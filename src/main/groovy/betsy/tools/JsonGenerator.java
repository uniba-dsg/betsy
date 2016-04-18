package betsy.tools;

import betsy.bpel.model.assertions.SoapFaultTestAssertion;
import betsy.bpel.model.assertions.XpathTestAssertion;
import betsy.bpel.model.steps.DelayTestStep;
import betsy.bpel.model.steps.DeployableCheckTestStep;
import betsy.bpel.model.steps.NotDeployableCheckTestStep;
import betsy.bpel.model.steps.SoapTestStep;
import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.model.BPMNTestCase;
import betsy.bpmn.model.BPMNTestStep;
import betsy.bpmn.model.BPMNTestVariable;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.Engine;
import betsy.common.model.engine.IsEngine;
import betsy.common.model.feature.Capability;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.model.feature.FeatureDimension;
import betsy.common.model.feature.Group;
import betsy.common.model.feature.Language;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.input.TestAssertion;
import betsy.common.model.input.TestCase;
import betsy.common.model.input.TestStep;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonGenerator {

    static class FeatureTreeJsonGenerator {
        private static void generatesConstructsJson() {
            JSONArray featureTree = new JSONArray();

            List<EngineIndependentProcess> processes = new LinkedList<>();
            processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
            processes.addAll(new BPMNProcessRepository().getByName("ALL"));
            convertProcess(featureTree, processes);

            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("feature-tree.json"), StandardOpenOption.CREATE)) {
                writer.append(featureTree.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void convertProcess(JSONArray rootArray, List<EngineIndependentProcess> processes) {
            Map<Capability, Map<Language, Map<Group, Map<Construct, List<EngineIndependentProcess>>>>> entries;
            entries = processes.stream().
                    collect(Collectors.groupingBy(FeatureDimension::getCapability,
                            Collectors.groupingBy(FeatureDimension::getLanguage,
                            Collectors.groupingBy(FeatureDimension::getGroup,
                            Collectors.groupingBy(FeatureDimension::getConstruct)))));

            JSONArray capabilityArray = rootArray;

            for(Map.Entry<Capability, Map<Language, Map<Group, Map<Construct, List<EngineIndependentProcess>>>>> entryCapability : entries.entrySet()) {
                Capability capability = entryCapability.getKey();
                JSONObject capabilityObject = new JSONObject();
                capabilityObject.put("name", capability.getName());
                capabilityObject.put("id", capability.getID());
                JSONArray languagesArray = new JSONArray();
                capabilityObject.put("languages", languagesArray);

                for(Map.Entry<Language, Map<Group, Map<Construct, List<EngineIndependentProcess>>>> entryLanguage : entryCapability.getValue().entrySet()) {
                    Language language = entryLanguage.getKey();
                    JSONObject languageObject = new JSONObject();
                    languageObject.put("name", language.getName());
                    languageObject.put("id", language.getID());
                    JSONArray groupsArray = new JSONArray();
                    languageObject.put("groups", groupsArray);

                    for(Map.Entry<Group, Map<Construct, List<EngineIndependentProcess>>> entryGroup : entryLanguage.getValue().entrySet()) {
                        Group group = entryGroup.getKey();

                        JSONObject groupObject = new JSONObject();
                        groupObject.put("name", group.getName());
                        groupObject.put("description", group.description);
                        groupObject.put("id", group.getID());
                        JSONArray constructsArray = new JSONArray();
                        groupObject.put("constructs", constructsArray);

                        for(Map.Entry<Construct, List<EngineIndependentProcess>> entryConstruct : entryGroup.getValue().entrySet()) {
                            Construct construct = entryConstruct.getKey();

                            JSONObject constructObject = new JSONObject();
                            constructObject.put("name", construct.getName());
                            constructObject.put("id", construct.getID());
                            constructObject.put("description", construct.description);
                            JSONArray featuresArray = new JSONArray();
                            constructObject.put("features", featuresArray);

                            for(EngineIndependentProcess process : entryConstruct.getValue()) {
                                Feature feature = process.getFeature();

                                groupObject.put("description", process.getGroup().description);

                                JSONObject featureObject = new JSONObject();
                                featureObject.put("id", feature.getID());
                                featureObject.put("name", feature.getName());
                                featureObject.put("description", feature.description);
                                featuresArray.put(featureObject);
                            }
                            constructsArray.put(constructObject);
                        }
                        groupsArray.put(groupObject);
                    }
                    languagesArray.put(languageObject);
                }
                capabilityArray.put(capabilityObject);
            }
        }
    }

    static class EnginesJsonGenerator {

        private static void generateEnginesJson() {
            JSONArray array = new JSONArray();

            for(EngineLifecycle e : getEngines()) {
                boolean excludeVirtualEngines = !(e instanceof VirtualEngineAPI);
                if(e instanceof IsEngine && excludeVirtualEngines) {
                    Engine engine = ((IsEngine) e).getEngineObject();
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
        TestsEngineIndependentJsonGenerator.generateTestsEngineIndependentJson();
    }

}

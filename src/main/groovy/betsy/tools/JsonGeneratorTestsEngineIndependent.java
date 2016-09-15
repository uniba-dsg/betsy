package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import pebl.test.Test;
import pebl.test.assertions.SoapFaultTestAssertion;
import pebl.test.assertions.XpathTestAssertion;
import pebl.test.steps.DelayTestStep;
import pebl.test.steps.DeployableCheckTestStep;
import pebl.test.steps.NotDeployableCheckTestStep;
import pebl.test.steps.soap.SoapTestStep;
import betsy.bpmn.model.BPMNTestCase;
import pebl.test.steps.VariableBasedTestStep;
import pebl.test.steps.Variable;
import pebl.test.partner.ExternalWSDLTestPartner;
import pebl.test.partner.NoTestPartner;
import pebl.test.TestAssertion;
import pebl.test.TestCase;
import pebl.test.TestPartner;
import pebl.test.TestStep;
import pebl.test.partner.InternalWSDLTestPartner;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import pebl.test.steps.soap.WsdlService;

class JsonGeneratorTestsEngineIndependent {

    public static void generateTestsEngineIndependentJson(Path folder) {
        JSONArray array = new JSONArray();

        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        convertProcess(array, processes);

        try (BufferedWriter writer = Files.newBufferedWriter(folder.resolve("tests-engine-independent.json"), StandardOpenOption.CREATE)) {
            writer.append(array.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateTestsEngineIndependentJsonWithReallyAllProcesses(Path folder) {
        JSONArray array = new JSONArray();

        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        convertProcess(array, processes);

        try (BufferedWriter writer = Files.newBufferedWriter(folder.resolve("tests-engine-independent.json"), StandardOpenOption.CREATE)) {
            writer.append(array.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertProcess(JSONArray array, List<Test> processes) {
        for (Test p : processes) {
            array.put(createTestObject(p));
        }
    }

    private static JSONObject createTestObject(Test p) {
        JSONObject testObject = new JSONObject();
        testObject.put("name", p.getName());
        testObject.put("description", p.getDescription());

        testObject.put("engineIndependentFiles", createEngineIndependentFilesArray(p));

        testObject.put("language", p.getProcessLanguage().name());
        testObject.put("featureID", p.getFeature().getID());

        JSONArray testCasesArray = new JSONArray();
        for (TestCase testCase : p.getTestCases()) {
            testCasesArray.put(createTestCaseObject(testCase));
        }
        testObject.put("testCases", testCasesArray);

        JSONArray testPartnersArray = new JSONArray();
        for (TestPartner testPartner : p.getTestPartners()) {
            testPartnersArray.put(createTestPartnerObject(testPartner));
        }
        testObject.put("testPartners", testPartnersArray);

        return testObject;
    }

    private static JSONObject createTestPartnerObject(TestPartner testPartner) {
        JSONObject testPartnerObject = new JSONObject();

        if (testPartner instanceof InternalWSDLTestPartner) {
            InternalWSDLTestPartner internalWsdlTestPartner = (InternalWSDLTestPartner) testPartner;

            testPartnerObject.put("type", "WSDL");
            testPartnerObject.put("external", false);

            testPartnerObject.put("interfaceDescription", internalWsdlTestPartner.interfaceDescription);
            testPartnerObject.put("publishedUrl", internalWsdlTestPartner.publishedUrl);
            testPartnerObject.put("wsdlUrl", internalWsdlTestPartner.getWSDLUrl());

            JSONArray rulesArray = new JSONArray();
            for (InternalWSDLTestPartner.OperationInputOutputRule operationInputOutputRule : internalWsdlTestPartner.getRules()) {
                JSONObject ruleObject = new JSONObject();
                ruleObject.put("operation", operationInputOutputRule.operation);

                InternalWSDLTestPartner.Input input = operationInputOutputRule.input;
                if (input instanceof InternalWSDLTestPartner.AnyInput) {
                    JSONObject anyInputObject = new JSONObject();
                    anyInputObject.put("type", "any");
                    ruleObject.put("input", anyInputObject);
                } else if (input instanceof InternalWSDLTestPartner.IntegerInput) {
                    JSONObject integerInputObject = new JSONObject();
                    integerInputObject.put("type", "integer");
                    integerInputObject.put("value", ((InternalWSDLTestPartner.IntegerInput) input).value);
                    ruleObject.put("input", integerInputObject);
                } else {
                    throw new IllegalStateException();
                }

                InternalWSDLTestPartner.Output output = operationInputOutputRule.output;
                if (output instanceof InternalWSDLTestPartner.IntegerOutputWithStatusCode) {
                    JSONObject object = new JSONObject();
                    object.put("type", "integer");
                    object.put("value", ((InternalWSDLTestPartner.IntegerOutputWithStatusCode) output).value);
                    object.put("statusCode", ((InternalWSDLTestPartner.IntegerOutputWithStatusCode) output).statusCode);
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.IntegerOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "integer");
                    object.put("value", ((InternalWSDLTestPartner.IntegerOutput) output).value);
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.RawOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "raw");
                    object.put("value", ((InternalWSDLTestPartner.RawOutput) output).value);
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.FaultOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "fault");
                    object.put("value", ((InternalWSDLTestPartner.FaultOutput) output).variant);
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.TimeoutInsteadOfOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "timeout");
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.EchoInputAsOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "echo");
                    ruleObject.put("output", object);
                } else if (output instanceof InternalWSDLTestPartner.IntegerOutputBasedOnScriptResult) {
                    JSONObject object = new JSONObject();
                    object.put("type", "script");
                    object.put("value", ((InternalWSDLTestPartner.IntegerOutputBasedOnScriptResult) output).script);
                    ruleObject.put("output", object);
                } else {
                    // do nothing
                }

                rulesArray.put(ruleObject);

            }
            testPartnerObject.put("rules", rulesArray);

        } else if(testPartner instanceof ExternalWSDLTestPartner){
            testPartnerObject.put("type", "WSDL");
            testPartnerObject.put("external", true);

            testPartnerObject.put("interfaceDescription", ((ExternalWSDLTestPartner) testPartner).wsdl);
            testPartnerObject.put("publishedUrl", ((ExternalWSDLTestPartner) testPartner).url);
            testPartnerObject.put("wsdlUrl", ((ExternalWSDLTestPartner) testPartner).getWSDLUrl());
        } else if(testPartner instanceof NoTestPartner) {
            testPartnerObject.put("type", "NONE");
        }

        return testPartnerObject;
    }

    private static JSONObject createTestCaseObject(TestCase testCase) {
        JSONObject testCaseObject = new JSONObject();

        testCaseObject.put("number", testCase.getNumber());
        testCaseObject.put("name", testCase.getName());

        if (testCase instanceof BPMNTestCase) {
            testCaseObject.put("testSteps", createBPMNTestStepArray((BPMNTestCase) testCase));
        } else {
            testCaseObject.put("testSteps", createBPELTestStepArray(testCase));
        }
        return testCaseObject;
    }

    private static JSONArray createBPELTestStepArray(TestCase testCase) {
        JSONArray testStepArray = new JSONArray();

        for (TestStep testStep : testCase.getTestSteps()) {
            JSONObject testStepObject = new JSONObject();
            testStepObject.put("description", testStep.getDescription());

            if (testStep instanceof DelayTestStep) {
                testStepObject.put("type", testStep.getClass().getSimpleName());
                testStepObject.put("delay", ((DelayTestStep) testStep).getTimeToWaitAfterwards());
            } else if (testStep instanceof DeployableCheckTestStep) {
                JSONArray assertionsArray = new JSONArray();
                testStepObject.put("assertions", assertionsArray);
                JSONObject assertionObject = new JSONObject();
                assertionObject.put("type", "DeployableAssertion");
                assertionsArray.put(assertionObject);
            } else if (testStep instanceof NotDeployableCheckTestStep) {
                JSONArray assertionsArray = new JSONArray();
                testStepObject.put("assertions", assertionsArray);
                JSONObject assertionObject = new JSONObject();
                assertionObject.put("type", "NotDeployableAssertion");
                assertionsArray.put(assertionObject);
            } else if (testStep instanceof SoapTestStep) {
                testStepObject.put("type", testStep.getClass().getSimpleName());
                testStepObject.put("input", ((SoapTestStep) testStep).getInput());
                if (((SoapTestStep) testStep).getOperation() != null) {
                    testStepObject.put("operation", ((SoapTestStep) testStep).getOperation().getName());
                }

                testStepObject.put("testPartner", ((SoapTestStep) testStep).getService().equals(new WsdlService("testInterface")));
                testStepObject.put("oneWay", ((SoapTestStep) testStep).getOperation().isOneWay());

                JSONArray assertionsArray = new JSONArray();
                testStepObject.put("assertions", assertionsArray);
                for (TestAssertion assertion : ((SoapTestStep) testStep).getAssertions()) {
                    JSONObject assertionObject = new JSONObject();
                    assertionObject.put("type", assertion.getClass().getSimpleName());
                    if (assertion instanceof SoapFaultTestAssertion) {
                        assertionObject.put("faultString", ((SoapFaultTestAssertion) assertion).getFaultString());
                    } else if (assertion instanceof XpathTestAssertion) {
                        assertionObject.put("value", ((XpathTestAssertion) assertion).getExpectedOutput());
                        assertionObject.put("xpathExpression", ((XpathTestAssertion) assertion).getXpathExpression());
                    }
                    assertionsArray.put(assertionObject);
                }

            }
            testStepArray.put(testStepObject);
        }
        return testStepArray;
    }

    private static JSONArray createBPMNTestStepArray(BPMNTestCase testCase) {
        BPMNTestCase bpmnTestCase = testCase;
        VariableBasedTestStep testStep = bpmnTestCase.getTestStep();
        JSONObject testStepObject = new JSONObject();
        testStepObject.put("type", testStep.getClass().getSimpleName());
        testStepObject.put("description", testStep.getDescription());
        testStep.getDelay().ifPresent(delay -> testStepObject.put("delay", delay));
        JSONArray inputArray = new JSONArray();
        testStepObject.put("inputs", inputArray);
        for (Variable var : bpmnTestCase.getVariables()) {
            JSONObject varObject = new JSONObject();
            varObject.put("name", var.getName());
            varObject.put("value", var.getValue());
            varObject.put("type", var.getType());
            inputArray.put(varObject);
        }

        JSONArray assertionsArray = new JSONArray();
        testStepObject.put("assertions", assertionsArray);
        for (String assertion : bpmnTestCase.getAssertions()) {
            JSONObject varObject = new JSONObject();
            varObject.put("type", "TraceTestAssertion");
            varObject.put("trace", assertion);
            assertionsArray.put(varObject);
        }

        JSONArray testStepArray = new JSONArray();
        testStepArray.put(testStepObject);
        return testStepArray;
    }

    private static JSONArray createEngineIndependentFilesArray(Test p) {
        JSONArray engineIndependentFiles = new JSONArray();
        engineIndependentFiles.put(p.getProcess().toString());
        for (Path path : p.getFiles()) {
            engineIndependentFiles.put(path.toString());
        }
        return engineIndependentFiles;
    }

}

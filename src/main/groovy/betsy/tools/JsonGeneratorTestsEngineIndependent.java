package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import pebl.benchmark.test.Test;
import pebl.benchmark.test.assertions.SoapFaultTestAssertion;
import pebl.benchmark.test.assertions.TraceTestAssertion;
import pebl.benchmark.test.assertions.XpathTestAssertion;
import pebl.benchmark.test.partner.rules.AnyInput;
import pebl.benchmark.test.partner.rules.EchoInputAsOutput;
import pebl.benchmark.test.partner.rules.SoapFaultOutput;
import pebl.benchmark.test.partner.rules.Input;
import pebl.benchmark.test.partner.rules.IntegerInput;
import pebl.benchmark.test.partner.rules.IntegerOutput;
import pebl.benchmark.test.partner.rules.IntegerOutputBasedOnScriptResult;
import pebl.benchmark.test.partner.rules.IntegerOutputWithStatusCode;
import pebl.benchmark.test.partner.rules.OperationInputOutputRule;
import pebl.benchmark.test.partner.rules.Output;
import pebl.benchmark.test.partner.rules.RawOutput;
import pebl.benchmark.test.partner.rules.TimeoutInsteadOfOutput;
import pebl.benchmark.test.steps.DelayTestStep;
import pebl.benchmark.test.steps.DeployableCheckTestStep;
import pebl.benchmark.test.steps.NotDeployableCheckTestStep;
import pebl.benchmark.test.steps.GatherTracesTestStep;
import pebl.benchmark.test.steps.vars.ProcessStartWithVariablesTestStep;
import pebl.benchmark.test.steps.soap.SoapTestStep;
import pebl.benchmark.test.steps.vars.Variable;
import pebl.benchmark.test.partner.ExternalWSDLTestPartner;
import pebl.benchmark.test.partner.NoTestPartner;
import pebl.benchmark.test.TestAssertion;
import pebl.benchmark.test.TestCase;
import pebl.benchmark.test.TestPartner;
import pebl.benchmark.test.TestStep;
import pebl.benchmark.test.partner.InternalWSDLTestPartner;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import pebl.benchmark.test.steps.soap.WsdlService;

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

        testObject.put("language", p.getProcessLanguage().getID());
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

            testPartnerObject.put("interfaceDescription", internalWsdlTestPartner.getInterfaceDescription());
            testPartnerObject.put("publishedUrl", internalWsdlTestPartner.getPublishedUrl());
            testPartnerObject.put("wsdlUrl", internalWsdlTestPartner.getWSDLUrl());

            JSONArray rulesArray = new JSONArray();
            for (OperationInputOutputRule operationInputOutputRule : internalWsdlTestPartner.getRules()) {
                JSONObject ruleObject = new JSONObject();
                ruleObject.put("operation", operationInputOutputRule.getOperation());

                Input input = operationInputOutputRule.getInput();
                if (input instanceof AnyInput) {
                    JSONObject anyInputObject = new JSONObject();
                    anyInputObject.put("type", "any");
                    ruleObject.put("input", anyInputObject);
                } else if (input instanceof IntegerInput) {
                    JSONObject integerInputObject = new JSONObject();
                    integerInputObject.put("type", "integer");
                    integerInputObject.put("value", ((IntegerInput) input).getValue());
                    ruleObject.put("input", integerInputObject);
                } else {
                    throw new IllegalStateException();
                }

                Output output = operationInputOutputRule.getOutput();
                if (output instanceof IntegerOutputWithStatusCode) {
                    JSONObject object = new JSONObject();
                    object.put("type", "integer");
                    object.put("value", ((IntegerOutputWithStatusCode) output).getValue());
                    object.put("statusCode", ((IntegerOutputWithStatusCode) output).getStatusCode());
                    ruleObject.put("output", object);
                } else if (output instanceof IntegerOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "integer");
                    object.put("value", ((IntegerOutput) output).getValue());
                    ruleObject.put("output", object);
                } else if (output instanceof RawOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "raw");
                    object.put("value", ((RawOutput) output).getValue());
                    ruleObject.put("output", object);
                } else if (output instanceof SoapFaultOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "fault");
                    object.put("value", ((SoapFaultOutput) output).getVariant());
                    ruleObject.put("output", object);
                } else if (output instanceof TimeoutInsteadOfOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "timeout");
                    ruleObject.put("output", object);
                } else if (output instanceof EchoInputAsOutput) {
                    JSONObject object = new JSONObject();
                    object.put("type", "echo");
                    ruleObject.put("output", object);
                } else if (output instanceof IntegerOutputBasedOnScriptResult) {
                    JSONObject object = new JSONObject();
                    object.put("type", "script");
                    object.put("value", ((IntegerOutputBasedOnScriptResult) output).getScript());
                    ruleObject.put("output", object);
                } else {
                    // do nothing
                }

                rulesArray.put(ruleObject);

            }
            testPartnerObject.put("rules", rulesArray);

        } else if (testPartner instanceof ExternalWSDLTestPartner) {
            testPartnerObject.put("type", "WSDL");
            testPartnerObject.put("external", true);

            testPartnerObject.put("interfaceDescription", ((ExternalWSDLTestPartner) testPartner).getWsdl());
            testPartnerObject.put("publishedUrl", ((ExternalWSDLTestPartner) testPartner).getUrl());
            testPartnerObject.put("wsdlUrl", ((ExternalWSDLTestPartner) testPartner).getWSDLUrl());
        } else if (testPartner instanceof NoTestPartner) {
            testPartnerObject.put("type", "NONE");
        }

        return testPartnerObject;
    }

    private static JSONObject createTestCaseObject(TestCase testCase) {
        JSONObject testCaseObject = new JSONObject();

        testCaseObject.put("number", testCase.getNumber());
        testCaseObject.put("name", testCase.getName());

        testCaseObject.put("testSteps", createBPELTestStepArray(testCase));

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

            } else if (testStep instanceof GatherTracesTestStep) {
                testStepObject.put("type", testStep.getClass().getSimpleName());
                JSONArray assertionsArray = new JSONArray();
                testStepObject.put("assertions", assertionsArray);
                for (TestAssertion assertion : ((GatherTracesTestStep) testStep).getAssertions()) {
                    JSONObject assertionObject = new JSONObject();
                    assertionObject.put("type", assertion.getClass().getSimpleName());
                    if (assertion instanceof TraceTestAssertion) {
                        assertionObject.put("trace", ((TraceTestAssertion) assertion).getTrace().getValue());
                    }
                    assertionsArray.put(assertionObject);
                }

            } else if (testStep instanceof ProcessStartWithVariablesTestStep) {
                testStepObject.put("type", testStep.getClass().getSimpleName());
                testStepObject.put("process", ((ProcessStartWithVariablesTestStep) testStep).getProcess());
                JSONArray assertionsArray = new JSONArray();
                testStepObject.put("variables", assertionsArray);
                for (Variable variable : ((ProcessStartWithVariablesTestStep) testStep).getVariables()) {
                    JSONObject assertionObject = new JSONObject();
                    testStepObject.put("value", variable.getValue());
                    testStepObject.put("name", variable.getName());
                    testStepObject.put("type", variable.getType());

                    assertionsArray.put(assertionObject);

                }

            }
            testStepArray.put(testStepObject);
        }
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

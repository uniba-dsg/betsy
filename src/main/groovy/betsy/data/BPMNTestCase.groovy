package betsy.data

import betsy.data.steps.BPMNTestStep
import org.json.JSONObject

class BPMNTestCase {

    int number
    List<BPMNTestStep> testSteps = []

    //variables to be sent for a test case
    JSONObject variables

    public BPMNTestCase(){
        number = 1
    }

    public BPMNTestCase(int number){
        this.number = number
    }

    public BPMNTestCase addStep(BPMNTestStep testStep){
        testSteps.add(testStep)

        this
    }

    public BPMNTestCase buildSimple(){
        addStep(new BPMNTestStep().assertSuccess())
    }

    public BPMNTestCase buildAnd(){
        addStep(new BPMNTestStep().assertAnd())
    }

    public BPMNTestCase buildXorTrue(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", true)
        value1.put("type", "Boolean")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertXorTrue())

    }

    public BPMNTestCase buildXorFalse(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", false)
        value1.put("type", "Boolean")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertXorFalse())
    }

    public BPMNTestCase buildOrSingleFlow(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", false)
        value1.put("type", "Boolean")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertOrSingleFlow())
    }

    public BPMNTestCase buildOrMultiFlow(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", true)
        value1.put("type", "Boolean")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertOrMultiFlow())
    }

}

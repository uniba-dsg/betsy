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

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "a")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertAnd())
    }

    public BPMNTestCase buildXorTrue(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "a")
        value1.put("type", "String")
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
        value1.put("value", "b")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertXorFalse())
    }

    public BPMNTestCase buildBothFalse(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "c")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertRuntimeException())
    }

    public BPMNTestCase buildXorWithDefaultBothFalse(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "c")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertDefault())
    }

    public BPMNTestCase buildXorBothTrue(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "ab")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertXorTrue())
    }

    public BPMNTestCase buildOrSingleFlow1(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "a")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertOrSingleFlow1())
    }

    public BPMNTestCase buildOrSingleFlow2(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "b")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertOrSingleFlow2())
    }

    public BPMNTestCase buildOrMultiFlow(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "ab")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertOrMultiFlow())
    }

    public BPMNTestCase buildDefault(){

        variables = new JSONObject()
        JSONObject value1 = new JSONObject()
        JSONObject value2 = new JSONObject()
        value1.put("value", "c")
        value1.put("type", "String")
        value2.put("value", number)
        value2.put("type","Integer")
        variables.put("test", value1)
        variables.put("testCaseNumber", value2)

        addStep(new BPMNTestStep().assertDefault())
    }

}

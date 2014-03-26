package betsy.data


import org.json.JSONObject

class BPMNTestCase {

    private int number
    private List<String> assertions = []

    private boolean selfStarting = false
    private int delay = 0

    //variables to be sent for a test case
    JSONObject variables

    public BPMNTestCase(){
        number = 1
        initializeTestCaseNumber()
    }

    public BPMNTestCase(int number){
        this.number = number
        initializeTestCaseNumber()
    }

    private void initializeTestCaseNumber(){
        variables = new JSONObject()
        JSONObject value = new JSONObject()
        value.put("value", number)
        value.put("type","Integer")
        variables.put("testCaseNumber", value)
    }

    //add inputs
    private BPMNTestCase addInputTestString(String value){
        JSONObject jsonObject = new JSONObject()
        jsonObject.put("value", value)
        jsonObject.put("type", "String")
        variables.put("test", jsonObject)

        this
    }

    public BPMNTestCase inputA(){
        addInputTestString("a")
    }

    public BPMNTestCase inputB(){
        addInputTestString("b")
    }

    public BPMNTestCase inputAB(){
        addInputTestString("ab")
    }

    public BPMNTestCase inputC(){
        addInputTestString("c")
    }

    //add assertions
    private BPMNTestCase addAssertions(String assertion){
        assertions.add(assertion)
        this
    }

    public BPMNTestCase assertSuccess(){
        addAssertions("success")
    }

    public BPMNTestCase assertTask1(){
        addAssertions("task1")
    }

    public BPMNTestCase assertTask2(){
        addAssertions("task2")
    }

    public BPMNTestCase assertTrue(){
        addAssertions("true")
    }

    public BPMNTestCase assertFalse(){
        addAssertions("false")
    }

    public BPMNTestCase assertDefault(){
        addAssertions("default")
    }

    public BPMNTestCase assertRuntimeException(){
        addAssertions("runtimeException")
    }

    public BPMNTestCase assertThrownErrorEvent(){
        addAssertions("thrownErrorEvent")
    }

    public BPMNTestCase assertSubprocess(){
        addAssertions("subprocess")
    }

    public BPMNTestCase assertLane1(){
        addAssertions("lane1")
    }

    public BPMNTestCase assertLane2(){
        addAssertions("lane2")
    }

    public BPMNTestCase assertMulti(){
        addAssertions("multi")
    }

    public BPMNTestCase assertInterrupted(){
        addAssertions("interrupted")
    }

    public BPMNTestCase assertSignaled(){
        addAssertions("signaled")
    }

    public BPMNTestCase assertNormalTask(){
        addAssertions("normalTask")
    }

    public BPMNTestCase assertErrorTask(){
        addAssertions("errorTask")
    }

    public BPMNTestCase assertTransactionTask(){
        addAssertions("transaction")
    }

    public BPMNTestCase assertCompensate(){
        addAssertions("compensate")
    }

    public BPMNTestCase assertNotInterrupted(){
        addAssertions("notInterrupted")
    }

    public BPMNTestCase assertStarted(){
        addAssertions("started")
    }

    public BPMNTestCase assertTimerInternal(){
        addAssertions("timerInternal")
    }

    public BPMNTestCase assertTimerExternal(){
        addAssertions("timerExternal")
    }

    //add options
    public BPMNTestCase optionDelay(int delay){
        this.delay = delay
        this
    }

    public BPMNTestCase optionSelfStarting(){
        selfStarting = true
        this
    }

    //Getter
    boolean getSelfStarting() {
        selfStarting
    }

    int getNumber() {
        number
    }

    int getDelay() {
        delay
    }

    List<String> getAssertions(){
        assertions
    }

    @Override
    public String toString(){
        String string = "test${number}Assert"
        for (String assertion : assertions){
            string += assertion.capitalize()
        }
        return string
    }
}

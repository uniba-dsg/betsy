package betsy.data.steps

class BPMNTestStep {

    /**
     * List of Assertions
     */
    List<String> assertions = []

    public BPMNTestStep addAssertions(String assertion){
        assertions.add(assertion)

        this
    }

    public BPMNTestStep assertXorTrue(){
        assertTrue()
        assertSuccess()
    }

    public BPMNTestStep assertXorFalse(){
        assertFalse()
        assertSuccess()
    }

    public BPMNTestStep assertAnd(){
        assertTask1()
        assertTask2()
        assertSuccess()
    }

    public BPMNTestStep assertOrSingleFlow1(){
        assertTask1()
        assertSuccess()
    }

    public BPMNTestStep assertOrSingleFlow2(){
        assertTask2()
        assertSuccess()
    }

    public BPMNTestStep assertParallelInExclusiveOut(){
        assertTask1()
        assertSuccess()
        assertTask2()
        assertSuccess()
    }

    public BPMNTestStep assertOrMultiFlow(){
        assertTask1()
        assertTask2()
        assertSuccess()
    }

    public BPMNTestStep assertSuccess(){
        addAssertions("success")
    }

    public BPMNTestStep assertTask1(){
        addAssertions("task1")
    }

    public BPMNTestStep assertTask2(){
        addAssertions("task2")
    }

    public BPMNTestStep assertTrue(){
        addAssertions("true")
    }

    public BPMNTestStep assertFalse(){
        addAssertions("false")
    }

    public BPMNTestStep assertDefault(){
        addAssertions("default")
        assertSuccess()
    }

    public BPMNTestStep assertRuntimeException(){
        addAssertions("runtimeException")
    }

    public BPMNTestStep assertThrownErrorEvent(){
        addAssertions("thrownErrorEvent")
    }

    public BPMNTestStep assertSubprocess(){
        addAssertions("subprocess")
    }
}

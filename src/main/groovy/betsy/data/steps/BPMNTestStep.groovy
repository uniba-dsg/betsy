package betsy.data.steps

/**
 * Created with IntelliJ IDEA.
 * User: stmcasar
 * Date: 13.03.14
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
class BPMNTestStep {

    /**
     * List of Assertions
     */
    List<String> assertions = []

    public addAssertions(String assertion){
        assertions.add(assertion)

        this
    }

    public void assertXorTrue(){
        assertTrue()
        assertSuccess()
    }

    public void assertXorFalse(){
        assertFalse()
        assertSuccess()
    }

    public void assertAnd(){
        assertTask1()
        assertTask2()
        assertSuccess()
    }

    public void assertOrSingleFlow(){
        assertTask1()
        assertSuccess()
    }

    public void assertOrMultiFlow(){
        assertTask1()
        assertTask2()
        assertSuccess()
    }

    public void assertSuccess(){
        assertions << "success"
    }

    public void assertTask1(){
        assertions << "task1"
    }

    public void assertTask2(){
        assertions << "task2"
    }

    public void assertTrue(){
        assertions << "true"
    }

    public void assertFalse(){
        assertions << "true"
    }

}

package betsy.bpmn;

import pebl.test.Test;
import configuration.bpmn.BPMNProcessRepository;

import static org.junit.Assert.assertEquals;

public class BPMNProcessAssertionsTest {

    @org.junit.Test
    public void test() {
        Test process = new BPMNProcessRepository().getByName("SequenceFlow").get(0);
        assertEquals("basics", process.getGroup().getName());
    }

}

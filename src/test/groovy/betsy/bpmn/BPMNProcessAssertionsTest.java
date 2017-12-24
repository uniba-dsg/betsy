package betsy.bpmn;

import configuration.bpmn.BPMNProcessRepository;
import pebl.benchmark.test.Test;

import static org.junit.Assert.assertEquals;

public class BPMNProcessAssertionsTest {

    @org.junit.Test
    public void test() {
        Test process = new BPMNProcessRepository().getByName("SequenceFlow").get(0);
        assertEquals("basics", process.getGroup().getName());
    }

}

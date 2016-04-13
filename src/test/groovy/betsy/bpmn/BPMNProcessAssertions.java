package betsy.bpmn;

import betsy.common.model.EngineIndependentProcess;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BPMNProcessAssertions {

    @Test
    public void test() {
        EngineIndependentProcess process = new BPMNProcessRepository().getByName("SequenceFlow").get(0);
        assertEquals("basics", process.getGroup().getName());
    }

}

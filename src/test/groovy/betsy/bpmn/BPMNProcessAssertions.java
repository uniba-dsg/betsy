package betsy.bpmn;

import betsy.bpmn.model.BPMNProcess;
import betsy.common.model.EngineIndependentProcess;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BPMNProcessAssertions {

    @Test
    public void test() {
        EngineIndependentProcess process = new BPMNProcessRepository().getByName("SequenceFlow").get(0);
        assertEquals("basics", process.getGroup().getName());
    }

}

package betsy;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SystemTest {

    @Test
    public void test_A_BpmnActivitiSequenceFlow() throws IOException {
        Main.main("bpmn", "activiti", "SequenceFlow");

        assertEquals("[SequenceFlow;activiti;basics;1;0;1;1]", Files.readAllLines(Paths.get("test/reports/results.csv")).toString());
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws IOException {
        Main.main("bpmn", "camunda720", "SequenceFlow");

        assertEquals("[SequenceFlow;camunda720;basics;1;0;1;1]", Files.readAllLines(Paths.get("test/reports/results.csv")).toString());
    }

    @Test
    public void test_B_BpelOdeSequence() throws IOException, InterruptedException {
        Main.main("bpel", "ode", "sequence", "-f", "test-ode");

        assertEquals("[Sequence;ode;structured;1;0;1;1]", Files.readAllLines(Paths.get("test-ode/reports/results.csv")).toString());
    }

}

package betsy;

import betsy.bpel.BPELMain;
import org.junit.FixMethodOrder;
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
    public void test_B1_BpelOdeSequence() throws IOException, InterruptedException {
        BPELMain.shutdownSoapUiAfterCompletion(false);
        BPELMain.main("ode", "sequence", "-f", "test-ode");

        assertEquals("[Sequence;ode;structured;1;0;1;1]", Files.readAllLines(Paths.get("test-ode/reports/results.csv")).toString());
    }

    @Test
    public void test_B2_BpelOrchestraSequence() throws IOException, InterruptedException {
        BPELMain.shutdownSoapUiAfterCompletion(false);
        BPELMain.main("orchestra", "sequence", "-f", "test-orchestra");

        assertEquals("[Sequence;orchestra;structured;1;0;1;1]", Files.readAllLines(Paths.get("test-orchestra/reports/results.csv")).toString());
    }

    @Test
    public void test_B3_BpelBpelgSequence() throws IOException, InterruptedException {
        BPELMain.shutdownSoapUiAfterCompletion(false);
        BPELMain.main("bpelg", "sequence", "-f", "test-bpelg");

        assertEquals("[Sequence;bpelg;structured;1;0;1;1]", Files.readAllLines(Paths.get("test-bpelg/reports/results.csv")).toString());
    }

    @Test
    public void test_B4_BpelWso320Sequence() throws IOException, InterruptedException {
        BPELMain.shutdownSoapUiAfterCompletion(true);
        BPELMain.main("wso2_v3_2_0", "sequence", "-f", "test-wso320");

        assertEquals("[Sequence;wso2_v3_2_0;structured;1;0;1;1]", Files.readAllLines(Paths.get("test-wso320/reports/results.csv")).toString());
    }

}

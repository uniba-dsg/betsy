package betsy;

import betsy.bpel.BPELMain;
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
        Main.main("bpmn", "-f", "test-activiti", "activiti", "SequenceFlow");

        assertEquals("[SequenceFlow;activiti;basics;1;0;1;1]", Files.readAllLines(Paths.get("test-activiti/reports/results.csv")).toString());
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws IOException {
        Main.main("bpmn", "-f", "test-camunda720", "camunda720", "SequenceFlow");

        assertEquals("[SequenceFlow;camunda720;basics;1;0;1;1]", Files.readAllLines(Paths.get("test-camunda720/reports/results.csv")).toString());
    }

    @Test
    public void test_B1_BpelOdeSequence() throws IOException, InterruptedException {
        testBPELEngine("ode");
    }

    @Test
    public void test_B1_BpelOdeInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("ode-in-memory");
    }

    @Test
    public void test_B1_BpelOde136Sequence() throws IOException, InterruptedException {
        testBPELEngine("ode136");
    }

    @Test
    public void test_B1_BpelOde136InMemorySequence() throws IOException, InterruptedException {
        testBPELEngine("ode136-in-memory");
    }

    @Test
    public void test_B2_BpelOrchestraSequence() throws IOException, InterruptedException {
        testBPELEngine("orchestra");
    }

    @Test
    public void test_B3_BpelBpelgSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg");
    }

    @Ignore
    @Test
    public void test_B4_BpelWso320Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_2_0");
    }

    @Test
    public void test_B5_BpelActiveBpelSequence() throws IOException, InterruptedException {
        testBPELEngine("active-bpel");
        BPELMain.shutdownSoapUiAfterCompletion(true);
    }

    private void testBPELEngine(String engine) throws IOException {
        BPELMain.shutdownSoapUiAfterCompletion(false);
        BPELMain.main(engine, "sequence", "-f", "test-" + engine);
        assertEquals("[Sequence;" + engine + ";structured;1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}

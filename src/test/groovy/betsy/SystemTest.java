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
        testBPMNEngine("activiti");
    }

    @Test
    public void test_A_BpmnActiviti5170SequenceFlow() throws IOException {
        testBPMNEngine("activiti5170");
    }

    @Test
    public void test_A_BpmnCamunda700SequenceFlow() throws IOException {
        testBPMNEngine("camunda");
    }

    @Test
    public void test_A_BpmnCamunda710SequenceFlow() throws IOException {
        testBPMNEngine("camunda710");
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws IOException {
        testBPMNEngine("camunda720");
    }

    @Test
    public void test_A_BpmnCamunda730SequenceFlow() throws IOException {
        testBPMNEngine("camunda730");
    }

    private void testBPMNEngine(String engine) throws IOException {
        Main.main("bpmn", "-f", "test-" + engine, engine, "SequenceFlow");
        assertEquals("[SequenceFlow;" + engine + ";basics;1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
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

    @Test
    public void test_B3_BpelBpelgInvokeSync() throws IOException, InterruptedException {
        testBPELEngine("bpelg", "basic", "Invoke-Sync");
    }

    @Test
    public void test_B3_BpelBpelgInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg-in-memory");
    }

    @Test
    public void test_B4_BpelWso212Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v2_1_2");
    }

    @Test
    public void test_B4_BpelWso300Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_0_0");
    }


    @Test
    public void test_B4_BpelWso310Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_1_0");
    }

    @Test
    public void test_B4_BpelWso320Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2_v3_2_0");
    }

    @Test
    public void test_B5_BpelOpenesb301StandaloneSequence() throws IOException, InterruptedException {
        testBPELEngine("openesb301standalone");
    }

    @Test
    public void test_B6_BpelActiveBpelSequence() throws IOException, InterruptedException {
        testBPELEngine("active-bpel");
        BPELMain.shutdownSoapUiAfterCompletion(true);
    }

    private void testBPELEngine(String engine) throws IOException {
        testBPELEngine(engine, "structured", "Sequence");
    }

    private void testBPELEngine(String engine, String group, String process) throws IOException {
        BPELMain.shutdownSoapUiAfterCompletion(false);
        BPELMain.main(engine, process, "-f", "test-" + engine);
        assertEquals("[" + process + ";" + engine + ";" + group + ";1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}

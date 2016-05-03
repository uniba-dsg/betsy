package betsy;

import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import betsy.common.tasks.FileTasks;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BPMNSystemTest extends AbstractSystemTest {

    @Test
    public void testBPMNEngine() throws IOException {
        testBPMNEngine("activiti__5_16_3");
    }

    @Test
    public void test_A_BpmnActiviti5170SequenceFlow() throws IOException {
        testBPMNEngine("activiti__5_17_0");
    }

    @Test
    public void test_A_BpmnActiviti5180SequenceFlow() throws IOException {
        testBPMNEngine("activiti__5_18_0");
    }

    @Test
    public void test_A_BpmnActiviti5190SequenceFlow() throws IOException {
        testBPMNEngine("activiti__5_19_0");
    }

    @Test
    public void test_A_BpmnActiviti5200SequenceFlow() throws IOException {
        testBPMNEngine("activiti__5_20_0");
    }

    @Test
    public void test_A_BpmnActiviti600Beta1SequenceFlow() throws IOException {
        testBPMNEngine("activiti__6_0_0_beta1");
    }

    @Test
    public void test_A_BpmnCamunda700SequenceFlow() throws IOException {
        testBPMNEngine("camunda__7_0_0");
    }

    @Test
    public void test_A_BpmnCamunda710SequenceFlow() throws IOException {
        testBPMNEngine("camunda__7_1_0");
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws IOException {
        testBPMNEngine("camunda__7_2_0");
    }

    @Test
    public void test_A_BpmnCamunda730SequenceFlow() throws IOException {
        testBPMNEngine("camunda__7_3_0");
    }

    @Test
    public void test_A_BpmnCamunda740SequenceFlow() throws IOException {
        testBPMNEngine("camunda__7_4_0");
    }

    @Test
    public void test_A_BpmnjBPMSequenceFlow() throws IOException {
        testBPMNEngine("jbpm__6_0_1");
    }

    @Test
    public void test_A_BpmnjBPM610SequenceFlow() throws IOException {
        testBPMNEngine("jbpm__6_1_0");
    }

    @Test
    public void test_A_BpmnjBPM620SequenceFlow() throws IOException {
        testBPMNEngine("jbpm__6_2_0");
    }

    @Test
    public void test_A_BpmnjBPM630SequenceFlow() throws IOException {
        testBPMNEngine("jbpm__6_3_0");
    }

    @Test
    public void test_A_BpmnjBPM640SequenceFlow() throws IOException {
        testBPMNEngine("jbpm__6_4_0");
    }

    private void testBPMNEngine(String engine) throws IOException {
        Main.main("bpmn", "-f", "test-" + engine, engine, "SequenceFlow");
        assertEquals("[SequenceFlow;" + engine + ";basics;1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}

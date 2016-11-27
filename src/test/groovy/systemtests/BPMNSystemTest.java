package systemtests;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import betsy.Main;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BPMNSystemTest extends AbstractSystemTest {

    @Test
    public void test_A_BpmnActiviti5163SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_16_3");
    }

    @Test
    public void test_A_BpmnActiviti5170SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_17_0");
    }

    @Test
    public void test_A_BpmnActiviti5180SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_18_0");
    }

    @Test
    public void test_A_BpmnActiviti5190SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_19_0");
    }

    @Test
    public void test_A_BpmnActiviti51902SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_19_0_2");
    }

    @Test
    public void test_A_BpmnActiviti5200SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_20_0");
    }

    @Test
    public void test_A_BpmnActiviti5210SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_21_0");
    }

    @Test
    public void test_A_BpmnActiviti5220SequenceFlow() throws Exception {
        testBPMNEngine("activiti__5_22_0");
    }

    @Test
    public void test_A_BpmnFlowable5220SequenceFlow() throws Exception {
        testBPMNEngine("flowable__5_22_0");
    }

    @Test
    public void test_A_BpmnCamunda700SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_0_0");
    }

    @Test
    public void test_A_BpmnCamunda710SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_1_0");
    }

    @Test
    public void test_A_BpmnCamunda720SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_2_0");
    }

    @Test
    public void test_A_BpmnCamunda730SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_3_0");
    }

    @Test
    public void test_A_BpmnCamunda740SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_4_0");
    }

    @Test
    public void test_A_BpmnCamunda750SequenceFlow() throws Exception {
        testBPMNEngine("camunda__7_5_0");
    }

    @Test
    public void test_A_BpmnjBPMSequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_0_1");
    }

    @Test
    public void test_A_BpmnjBPM610SequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_1_0");
    }

    @Test
    public void test_A_BpmnjBPM620SequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_2_0");
    }

    @Test
    public void test_A_BpmnjBPM630SequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_3_0");
    }

    @Test
    public void test_A_BpmnjBPM640SequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_4_0");
    }

    @Test
    public void test_A_BpmnjBPM650SequenceFlow() throws Exception {
        testBPMNEngine("jbpm__6_5_0");
    }

    private void testBPMNEngine(String engine) throws Exception {
        Main.main("bpmn", "-f", "test-" + engine, engine, "SequenceFlow");
        assertEquals("[SequenceFlow;" + engine + ";basics;1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}

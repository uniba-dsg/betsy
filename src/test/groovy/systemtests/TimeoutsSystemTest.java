package systemtests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import betsy.Main;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TimeoutsSystemTest extends AbstractSystemTest{

    @Test
    public void test_B1_BpelOdeSequence() throws Exception, InterruptedException {
        testBPELEngine("ode__1_3_5");
    }

    @Test
    public void test_B1_BpelOdeInMemSequence() throws Exception, InterruptedException {
        testBPELEngine("ode__1_3_5__in-memory");
    }

    @Test
    public void test_B1_BpelOde136Sequence() throws Exception, InterruptedException {
        testBPELEngine("ode__1_3_6");
    }

    @Test
    public void test_B1_BpelOde136InMemorySequence() throws Exception, InterruptedException {
        testBPELEngine("ode__1_3_6__in-memory");
    }

    @Test
    public void test_B2_BpelOrchestraSequence() throws Exception, InterruptedException {
        testBPELEngine("orchestra__4_9");
    }

    @Test
    public void test_B3_BpelBpelgSequence() throws Exception, InterruptedException {
        testBPELEngine("bpelg__5_3");
    }

    @Test
    public void test_B3_BpelBpelgInMemSequence() throws Exception, InterruptedException {
        testBPELEngine("bpelg__5_3__in-memory");
    }

    @Test @Ignore("does not work on *nix when starting in the background as a deamon service")
    public void test_B4_BpelWso212Sequence() throws Exception, InterruptedException {
        testBPELEngine("wso2__2_1_2");
    }

    @Test
    public void test_B4_BpelWso300Sequence() throws Exception, InterruptedException {
        testBPELEngine("wso2__3_0_0");
    }

    @Test
    public void test_B4_BpelWso310Sequence() throws Exception, InterruptedException {
        testBPELEngine("wso2__3_1_0");
    }

    @Test
    public void test_B4_BpelWso320Sequence() throws Exception, InterruptedException {
        testBPELEngine("wso2__3_2_0");
    }

    @Test
    public void test_B5_A_BpelOpenesb305StandaloneSequence() throws Exception, InterruptedException {
        testBPELEngine("openesb__3_0_5");
    }

    @Test @Ignore("outdated revision")
    public void test_B5_B2_BpelOpenesb23Sequence() throws Exception, InterruptedException {
        testBPELEngine("openesb__2_3");
    }

    @Test
    public void test_B5__B3_BpelOpenesb231Sequence() throws Exception, InterruptedException {
        testBPELEngine("openesb__2_3_1");
    }

    @Test @Ignore("unstable")
    public void test_B5__B1_BpelOpenesbSequence() throws Exception, InterruptedException {
        testBPELEngine("openesb__2_2");
    }

    @Test
    public void test_B6_BpelActiveBpelSequence() throws Exception, InterruptedException {
        testBPELEngine("activebpel__5_0_2");
    }

    @Test @Ignore("older revision, possibly unstable")
    public void test_B7_BpelPetalsesbSequence() throws Exception, InterruptedException {
        testBPELEngine("petalsesb__4_0");
    }

    @Test @Ignore("unstable")
    public void test_B7_BpelPetalsesb41Sequence() throws Exception, InterruptedException {
        testBPELEngine("petalsesb__4_1");
    }

    @Test
    public void test_Locals_Sequence() throws Exception {
        testBPMNEngine("locals");
    }

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
    public void test_A_BpmnActiviti600Beta1SequenceFlow() throws Exception {
        testBPMNEngine("activiti__6_0_0_beta1");
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
    public void test_All_SequenceFlow() throws Exception {
        testBPMNEngine("all");
    }


    private void testBPELEngine(String engine) throws Exception {
        Main.main("bpel", engine, "sequence");
        Path path = Paths.get("timeout.properties");
        assertTrue("File should exist.", Files.exists(Paths.get("timeout.properties")));
        Files.delete(path);
    }

    private void testBPMNEngine(String engine) throws Exception {
        Main.main("bpmn", engine, "SequenceFlow");
        Path path = Paths.get("timeout.properties");
        assertTrue("File should exist.", Files.exists(path));
        Files.delete(path);
    }
}

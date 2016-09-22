package systemtests;

import betsy.bpel.BPELMain;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BPELSystemTest extends AbstractSystemTest{

    @BeforeClass
    public static void disableSoapUIShutdown() {
        BPELMain.shutdownSoapUiAfterCompletion(false);
    }

    @AfterClass
    public static void tearDownSoapUI() {
        SoapUIShutdownHelper.shutdownSoapUIForReal();
    }

    @Test
    public void test_B1_BpelOdeSequence() throws IOException, InterruptedException {
        testBPELEngine("ode__1_3_5");
    }

    @Test
    public void test_B1_BpelOdeInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("ode__1_3_5__in-memory");
    }

    @Test
    public void test_B1_BpelOde136Sequence() throws IOException, InterruptedException {
        testBPELEngine("ode__1_3_6");
    }

    @Test
    public void test_B1_BpelOde136InMemorySequence() throws IOException, InterruptedException {
        testBPELEngine("ode__1_3_6__in-memory");
    }

    @Test
    public void test_B2_BpelOrchestraSequence() throws IOException, InterruptedException {
        testBPELEngine("orchestra__4_9");
    }

    @Test
    public void test_B3_BpelBpelgSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg__5_3");
    }

    @Test
    public void test_B3_BpelBpelgInvokeSync() throws IOException, InterruptedException {
        testBPELEngine("bpelg__5_3", "basic", "Invoke-Sync");
    }

    @Test
    public void test_B3_BpelBpelgInMemSequence() throws IOException, InterruptedException {
        testBPELEngine("bpelg__5_3__in-memory");
    }

    @Test @Ignore("does not work on *nix when starting in the background as a deamon service")
    public void test_B4_BpelWso212Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2__2_1_2");
    }

    @Test
    public void test_B4_BpelWso300Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2__3_0_0");
    }

    @Test
    public void test_B4_BpelWso310Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2__3_1_0");
    }

    @Test
    public void test_B4_BpelWso320Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2__3_2_0");
    }

    @Test
    public void test_B4_BpelWso351Sequence() throws IOException, InterruptedException {
        testBPELEngine("wso2__3_5_1");
    }

    @Test
    public void test_B5_A_BpelOpenesb305StandaloneSequence() throws IOException, InterruptedException {
        testBPELEngine("openesb__3_0_5");
    }

    @Test
    public void test_B5_B2_BpelOpenesb23Sequence() throws IOException, InterruptedException {
        testBPELEngine("openesb__2_3");
    }

    @Test
    public void test_B5__B3_BpelOpenesb231Sequence() throws IOException, InterruptedException {
        testBPELEngine("openesb__2_3_1");
    }

    @Test
    public void test_B5__B1_BpelOpenesbSequence() throws IOException, InterruptedException {
        testBPELEngine("openesb__2_2");
    }

    @Test
    public void test_B6_BpelActiveBpelSequence() throws IOException, InterruptedException {
        testBPELEngine("activebpel__5_0_2");
    }

    @Test @Ignore("older revision, possibly unstable")
    public void test_B7_BpelPetalsesbSequence() throws IOException, InterruptedException {
        testBPELEngine("petalsesb__4_0");
    }

    @Test @Ignore("unstable")
    public void test_B7_BpelPetalsesb41Sequence() throws IOException, InterruptedException {
        testBPELEngine("petalsesb__4_1");
    }


    private void testBPELEngine(String engine) throws IOException {
        testBPELEngine(engine, "structured", "Sequence");
    }

    private void testBPELEngine(String engine, String group, String process) throws IOException {
        BPELMain.main(engine, process, "-f", "test-" + engine);
        assertEquals("[" + process + ";" + engine + ";" + group + ";1;0;1;1]", Files.readAllLines(Paths.get("test-" + engine + "/reports/results.csv")).toString());
    }

}

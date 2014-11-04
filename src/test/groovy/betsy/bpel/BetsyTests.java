package betsy.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.engines.Engine;
import betsy.bpel.engines.EnginePackageBuilder;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BetsyTests {
    @Test
    public void simulateATestRun() throws Exception {
        Engine engine = new MockEngine();
        List<BPELProcess> processes = new BPELProcessRepository().getByName("ALL");
        Betsy betsy = new Betsy();


        betsy.setEngines(Arrays.asList(engine));
        betsy.setProcesses(processes);
        betsy.setComposite(new MockComposite());
        betsy.execute();
    }

    public class MockEngine extends Engine {
        public void install() {
        }

        @Override
        public void uninstall() {

        }

        @Override
        public boolean isInstalled() {
            return false;
        }

        public void startup() {
        }

        public void shutdown() {
        }

        public boolean isRunning() {
            return false;
        }

        public void deploy(BPELProcess process) {
        }

        public void storeLogs(BPELProcess process) {
        }

        public void buildArchives(BPELProcess process) {
            new EnginePackageBuilder().createFolderAndCopyProcessFilesToTarget(process);
        }

        public String getEndpointUrl(BPELProcess process) {
            return "myendpoint";
        }

        public String getName() {
            return "mock";
        }

        public Path getXsltPath() {
            return Paths.get("unused_path");
        }

    }

    public class MockComposite extends Composite {
        @Override
        protected void buildTest(BPELProcess process) {

        }

        @Override
        protected void test(BPELProcess process) {

        }

    }
}

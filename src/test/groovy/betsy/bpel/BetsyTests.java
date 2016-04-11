package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.engines.BPELEnginePackageBuilder;
import betsy.bpel.model.BPELProcess;
import betsy.common.model.ProcessLanguage;
import betsy.common.model.Engine;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class BetsyTests {
    @Test
    public void simulateATestRun() throws Exception {
        AbstractBPELEngine engine = new MockEngine();
        List<BPELProcess> processes = BPELProcessRepository.INSTANCE.getByName("ALL");
        BPELBetsy betsy = new BPELBetsy();


        betsy.setEngines(Collections.singletonList(engine));
        betsy.setProcesses(processes);
        betsy.setTestFolder("test");
        betsy.setComposite(new MockComposite());
        betsy.execute();
    }

    public class MockEngine extends AbstractBPELEngine {
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
            new BPELEnginePackageBuilder().createFolderAndCopyProcessFilesToTarget(process);
        }

        public String getEndpointUrl(BPELProcess process) {
            return "myendpoint";
        }

        @Override
        public Engine getEngineId() {
            return new Engine(ProcessLanguage.BPEL, "mock", "1.0");
        }

        public Path getXsltPath() {
            return Paths.get("unused_path");
        }

        @Override
        public List<Path> getLogs() {
            return null;
        }
    }

    public class MockComposite extends BPELComposite {
        @Override
        protected void buildTest(BPELProcess process) {

        }

        @Override
        protected void test(BPELProcess process) {

        }

    }
}

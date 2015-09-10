package betsy.bpmn;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BPMNBetsyTests {
    @Test
    public void simulateATestRun() throws Exception {
        AbstractBPMNEngine engine = new MockEngine();

        List<BPMNProcess> processes = new BPMNProcessRepository().getByName("ALL");

        BPMNBetsy betsy = new BPMNBetsy();

        betsy.setEngines(Collections.singletonList(engine));
        betsy.setProcesses(processes);
        betsy.setTestFolder("test");
        betsy.execute();
    }

    public class MockEngine extends AbstractBPMNEngine {
        public void install() {
        }

        public void startup() {
        }

        public void shutdown() {
        }

        public boolean isRunning() {
            return false;
        }

        public void deploy(BPMNProcess process) {
        }

        public void storeLogs(BPMNProcess process) {
        }

        public void buildArchives(BPMNProcess process) {

        }

        public String getEndpointUrl(BPMNProcess process) {
            return "myendpoint";
        }

        public String getName() {
            return "mock";
        }

        public Path getXsltPath() {
            return Paths.get("unused_path");
        }

        @Override
        public void buildTest(BPMNProcess process) {

        }

        @Override
        public void testProcess(BPMNProcess process) {

        }

        @Override
        public List<Path> getLogs() {
            return null;
        }
    }
}

package betsy.bpmn;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.model.engine.Engine;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.ProcessLanguage;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class BPMNBetsyTests {
    @Test
    public void simulateATestRun() throws Exception {
        AbstractBPMNEngine engine = new MockEngine();

        List<EngineIndependentProcess> processes = new BPMNProcessRepository().getByName("ALL");

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

        @Override
        public Engine getEngineObject() {
            return new Engine(ProcessLanguage.BPMN, "mock","1.0");
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

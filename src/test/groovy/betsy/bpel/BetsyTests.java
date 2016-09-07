package betsy.bpel;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.engines.BPELEnginePackageBuilder;
import betsy.bpel.model.BPELProcess;
import betsy.common.model.engine.Engine;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.model.ProcessLanguage;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

public class BetsyTests {
    @Test
    public void simulateATestRun() throws Exception {
        AbstractBPELEngine engine = new MockEngine();
        List<EngineIndependentProcess> processes = BPELProcessRepository.INSTANCE.getByName("ALL");
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

        public void deploy(String name, Path path) {
        }

        @Override public boolean isDeployed(QName process) {
            return false;
        }

        @Override public void undeploy(QName process) {

        }

        public void storeLogs(BPELProcess process) {
        }

        public Path buildArchives(BPELProcess process) {
            new BPELEnginePackageBuilder().createFolderAndCopyProcessFilesToTarget(process);
            return process.getTargetPackageFilePath();
        }

        public String getEndpointUrl(String name) {
            return "myendpoint";
        }

        @Override
        public Engine getEngineObject() {
            return new Engine(ProcessLanguage.BPEL, "mock", "1.0", LocalDate.of(1, 1, 1), "Apache-2.0");
        }

        public Path getXsltPath() {
            return Paths.get("unused_path");
        }

        @Override
        public List<Path> getLogs() {
            return Collections.emptyList();
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

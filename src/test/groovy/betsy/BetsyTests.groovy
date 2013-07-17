package betsy

import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.data.engines.EnginePackageBuilder
import betsy.executables.Composite
import configuration.processes.BasicActivityProcesses
import configuration.processes.StructuredActivityProcesses
import org.junit.Test

class BetsyTests {

    class MockEngine extends Engine {
        void install() {}

        void startup() {}

        void shutdown() {}

        @Override
        void failIfRunning() {}

        void deploy(BetsyProcess process) {}

        void storeLogs(BetsyProcess process) {}

        void onPostDeployment(BetsyProcess process) {}

        @Override
        void buildArchives(BetsyProcess process) {
            new EnginePackageBuilder().createFolderAndCopyProcessFilesToTarget(process)
        }

        @Override
        String getEndpointUrl(BetsyProcess process) { "myendpoint" }

        String getName() { "mock" }

        String getXsltPath() { "unused_path"}
    }

    class MockComposite extends Composite {
        @Override
        protected void test(BetsyProcess process) {

        }
    }

    @Test
    public void simulateATestRun() {
        Engine engine = new MockEngine()
        Betsy betsy = new Betsy(engines: [engine], processes: [new StructuredActivityProcesses().SEQUENCE, new BasicActivityProcesses().INVOKE_SYNC], composite: new MockComposite())
        betsy.execute()
    }

}

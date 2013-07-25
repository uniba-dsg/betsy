package betsy

import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.data.engines.EnginePackageBuilder
import betsy.executables.Composite
import configuration.processes.Processes
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

        String getXsltPath() { "unused_path" }
    }

    class MockComposite extends Composite {
        @Override
        protected void test(BetsyProcess process) {

        }
    }

    @Test
    public void simulateATestRun() {
        Engine engine = new MockEngine()
        ArrayList<BetsyProcess> processes = new Processes().ALL
        Betsy betsy = new Betsy(engines: [engine], processes: processes, composite: new MockComposite())
        betsy.execute()
    }

}

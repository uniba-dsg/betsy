package betsy

import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.data.engines.EnginePackageBuilder
import betsy.executables.Composite
import configuration.ProcessRepository
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

class BetsyTests {

    class MockEngine extends Engine {
        void install() {}

        void startup() {}

        void shutdown() {}

        @Override
        void failIfRunning() {}

        void deploy(BetsyProcess process) {}

        void storeLogs(BetsyProcess process) {}

        @Override
        void buildArchives(BetsyProcess process) {
            new EnginePackageBuilder().createFolderAndCopyProcessFilesToTarget(process)
        }

        @Override
        String getEndpointUrl(BetsyProcess process) { "myendpoint" }

        String getName() { "mock" }

        Path getXsltPath() { Paths.get("unused_path") }
    }

    class MockComposite extends Composite {

        @Override
        protected void buildTest(BetsyProcess process) {

        }

        @Override
        protected void test(BetsyProcess process) {

        }
    }

    @Test
    public void simulateATestRun() {
        Engine engine = new MockEngine()
        ArrayList<BetsyProcess> processes = new ProcessRepository().getByName("ALL")
        Betsy betsy = new Betsy(engines: [engine], processes: processes, composite: new MockComposite())
        betsy.execute()
    }

}

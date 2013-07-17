package betsy.corebpel

import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.data.engines.EnginePackageBuilder
import corebpel.CoreBPEL

class CoreBPELEngineExtension {

    public static void extendEngine(Engine engine) {

        engine.packageBuilder = new EnginePackageBuilder() {

            @Override
            void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
                super.createFolderAndCopyProcessFilesToTarget(process)
                new CoreBPEL(temporaryDirectory: process.targetTmpPath, bpelFilePath: process.targetBpelFilePath).toCoreBPEL()
            }
        }

    }
}

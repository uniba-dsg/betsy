package betsy.corebpel

import betsy.data.BetsyProcess
import betsy.data.engines.Engine
import betsy.data.engines.EnginePackageBuilder
import corebpel.CoreBPEL

class CoreBPELEngineExtension {

    public static void extendEngine(Engine engine) {
        extendEngine(engine, CoreBPEL.XSL_SHEETS)
    }

    public static void extendEngine(Engine engine, String[] transformations) {
        for(String transformation : transformations){
            if(!CoreBPEL.XSL_SHEETS.any { it == transformation} ){
                throw new IllegalArgumentException("Given transformation $transformation is not a valid CoreBPEL transformation")
            }
        }

        engine.packageBuilder = new EnginePackageBuilder() {

            @Override
            void createFolderAndCopyProcessFilesToTarget(BetsyProcess process) {
                super.createFolderAndCopyProcessFilesToTarget(process)
                CoreBPEL coreBPEL = new CoreBPEL(temporaryDirectory: process.targetTmpPath, bpelFilePath: process.targetBpelFilePath)
                coreBPEL.toCoreBPEL(transformations)
            }
        }

    }
}

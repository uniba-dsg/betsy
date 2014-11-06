package betsy.bpel.corebpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.engines.Engine;
import betsy.bpel.engines.EnginePackageBuilder;
import corebpel.CoreBPEL;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CoreBPELEngineExtension {
    public static void extendEngine(Engine engine, final List<String> transformations) {

        List<String> element = transformations.stream().filter((t) -> !CoreBPEL.XSL_SHEETS.contains(t)).collect(Collectors.toList());
        if (!element.isEmpty()) {
            throw new IllegalArgumentException("Given transformations " + element + " are not a valid CoreBPEL transformations");
        }

        engine.setPackageBuilder(new EnginePackageBuilder() {
            @Override
            public void createFolderAndCopyProcessFilesToTarget(BPELProcess process) {
                super.createFolderAndCopyProcessFilesToTarget(process);
                CoreBPEL coreBPEL = new CoreBPEL(process.getTargetTmpPath(), process.getTargetProcessFilePath());

                try {
                    coreBPEL.toCoreBPEL(transformations);
                } catch (IOException | TransformerException e) {
                    throw new RuntimeException("Error during corebpel transformation", e);
                }
            }

        });
    }

}

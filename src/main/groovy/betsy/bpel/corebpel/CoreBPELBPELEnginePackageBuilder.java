package betsy.bpel.corebpel;

import betsy.bpel.engines.BPELEnginePackageBuilder;
import betsy.bpel.model.BPELProcess;
import corebpel.CoreBPEL;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CoreBPELBPELEnginePackageBuilder extends BPELEnginePackageBuilder {
    private final List<String> transformations;

    public CoreBPELBPELEnginePackageBuilder(List<String> transformations) {
        this.transformations = new ArrayList<>(Objects.requireNonNull(transformations));
    }

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

}

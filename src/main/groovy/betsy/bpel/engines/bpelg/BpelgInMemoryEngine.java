package betsy.bpel.engines.bpelg;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;

public class BpelgInMemoryEngine extends BpelgEngine {

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg-in-memory");
    }

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "bpelg", "5.3", "in-memory");
    }

}

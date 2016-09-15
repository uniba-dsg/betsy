package betsy.bpel.engines.bpelg;

import java.nio.file.Path;
import java.time.LocalDate;

import pebl.ProcessLanguage;
import betsy.common.model.engine.EngineExtended;
import betsy.common.util.ClasspathHelper;

public class BpelgInMemoryEngine extends BpelgEngine {

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg-in-memory");
    }

    @Override
    public EngineExtended getEngineObject() {
        // TODO this is the snapshot release, the real 5.3 was released on 2012-12-26
        return new EngineExtended(ProcessLanguage.BPEL, "bpelg", "5.3", "in-memory", LocalDate.of(2012, 4, 27), "GPL-2.0+");
    }

}

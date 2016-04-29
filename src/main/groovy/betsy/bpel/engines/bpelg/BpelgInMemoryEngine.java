package betsy.bpel.engines.bpelg;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.time.LocalDate;

public class BpelgInMemoryEngine extends BpelgEngine {

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/bpelg-in-memory");
    }

    @Override
    public Engine getEngineObject() {
        // TODO this is the snapshot release, the real 5.3 was released on 2012-12-26
        return new Engine(ProcessLanguage.BPEL, "bpelg", "5.3", "in-memory", LocalDate.of(2012, 4, 27));
    }

}

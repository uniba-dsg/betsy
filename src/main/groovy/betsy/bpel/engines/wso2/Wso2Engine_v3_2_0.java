package betsy.bpel.engines.wso2;

import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.*;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Wso2Engine_v3_2_0 extends Wso2Engine_v3_1_0 {

    @Override
    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/ode");
    }

    @Override
    public String getName() {
        return "wso2_v3_2_0";
    }

    public String getZipFileName() {
        return "wso2bps-3.2.0.zip";
    }

    public Path getCarbonHome() {
        return getServerPath().resolve("wso2bps-3.2.0");
    }

}

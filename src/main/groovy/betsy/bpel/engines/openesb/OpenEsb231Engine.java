package betsy.bpel.engines.openesb;

import betsy.common.engines.ProcessLanguage;
import betsy.common.model.Engine;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.OperatingSystem;

import java.nio.file.Paths;

public class OpenEsb231Engine extends OpenEsbEngine {

    @Override
    public Engine getEngineId() {
        return new Engine(ProcessLanguage.BPEL, "openesb", "2.3.1");
    }

    @Override
    public void install() {
        if(OperatingSystem.WINDOWS) {
            new OpenEsbInstaller(getServerPath(),
                    "openesb-v231-installer-windows.exe",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/state.xml.template")).install();
        } else {
            new OpenEsbInstaller(getServerPath(),
                    "openesb-v231-installer-linux.sh",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/state.xml.template")).install();
        }
    }

}

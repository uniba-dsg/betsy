package betsy.bpel.engines.openesb;

import java.time.LocalDate;

import betsy.common.model.ProcessLanguage;
import betsy.common.model.engine.Engine;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.OperatingSystem;

public class OpenEsb231Engine extends OpenEsbEngine {

    @Override
    public Engine getEngineObject() {
        return new Engine(ProcessLanguage.BPEL, "openesb", "2.3.1", LocalDate.of(2013, 10, 1), "CDDL-1.0");
    }

    @Override
    public void install() {
        if (OperatingSystem.WINDOWS) {
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

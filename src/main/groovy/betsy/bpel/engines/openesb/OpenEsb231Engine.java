package betsy.bpel.engines.openesb;

import java.time.LocalDate;

import betsy.common.model.engine.EngineExtended;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.OperatingSystem;
import pebl.ProcessLanguage;

public class OpenEsb231Engine extends OpenEsbEngine {

    @Override
    public EngineExtended getEngineObject() {
        return new EngineExtended(ProcessLanguage.BPEL, "openesb", "2.3.1", LocalDate.of(2013, 10, 1), "CDDL-1.0");
    }

    @Override
    public void install() {
        if (OperatingSystem.WINDOWS) {
            new OpenEsbInstaller(getServerPath(),
                    "openesb-v231-installer-windows.exe",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/windows_state.xml.template")).install();
        } else {
            new OpenEsbInstaller(getServerPath(),
                    "openesb-v231-installer-linux.sh",
                    ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/state.xml.template")).install();
        }
    }

}

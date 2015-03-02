package betsy.bpel.engines.openesb;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenEsb231Engine extends OpenEsbEngine {
    @Override
    public String getName() {
        return "openesb231";
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb");
    }

    @Override
    public void install() {
        new OpenEsbInstaller(Paths.get("server/openesb231"), "openesb-v231-installer-windows.exe",
                ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/state.xml.template")).install();
    }

}

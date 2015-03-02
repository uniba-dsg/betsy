package betsy.bpel.engines.openesb;

import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenEsb23Engine extends OpenEsbEngine {
    @Override
    public String getName() {
        return "openesb23";
    }

    public Path getXsltPath() {
        return ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb");
    }

    @Override
    public void install() {
        new OpenEsbInstaller(Paths.get("server/openesb23"), "openesb-v23-installer-windows.exe",
                ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb23/state.xml.template")).install();
    }

}

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
        OpenEsbInstaller installer = new OpenEsbInstaller();
        installer.setFileName("openesb-v231-installer-windows.exe");
        installer.setServerDir(Paths.get("server/openesb231"));
        installer.setStateXmlTemplate(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpel/openesb231/state.xml.template"));
        installer.install();
    }

}

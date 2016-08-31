package betsy.bpel.engines.petalsesb;

import java.nio.file.Path;
import java.util.Objects;

import betsy.bpel.model.BPELProcess;
import betsy.common.config.Configuration;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;

public class PetalsEsbCompositePackager {

    private final BPELProcess process;

    public PetalsEsbCompositePackager(BPELProcess process) {
        this.process = Objects.requireNonNull(process);
    }

    public void build() {
        createBinding();
        createComposite();
    }

    private void createComposite() {
        // create composite
        Path compositeDir = process.getTargetTmpPath().resolve("composite");
        Path compositeMetaDir = compositeDir.resolve("META-INF");
        FileTasks.mkdirs(compositeMetaDir);
        XSLTTasks.transform(process.getEngine().getXsltPath().resolve("create_composite_jbi_from_bpel.xsl"),
                process.getTargetProcessFilePath(), compositeMetaDir.resolve("jbi.xml"));
        FileTasks.copyFileIntoFolder(process.getTargetPackageFilePath(), compositeDir);
        FileTasks.copyFileIntoFolder(getBindingArchive(), compositeDir);

        FileTasks.replaceTokensInFolder(compositeDir, "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));
        FileTasks.replaceTokensInFolder(compositeMetaDir, "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));

        // build composite
        ZipTasks.zipFolder(process.getTargetPackageCompositeFilePath(), compositeDir);
    }

    public void createBinding() {
        Path bindingDir = process.getTargetTmpPath().resolve("binding");
        Path bindingMetaDir = bindingDir.resolve("META-INF");
        FileTasks.mkdirs(bindingDir);
        FileTasks.mkdirs(bindingMetaDir);

        XSLTTasks.transform(process.getEngine().getXsltPath().resolve("create_binding_jbi_from_bpel.xsl"),
                process.getTargetProcessFilePath(), bindingMetaDir.resolve("jbi.xml"));

        FileTasks.copyMatchingFilesIntoFolder(process.getTargetProcessPath(), bindingDir, "*.xsd");
        FileTasks.copyMatchingFilesIntoFolder(process.getTargetProcessPath(), bindingDir, "*.wsdl");

        FileTasks.replaceTokensInFolder(bindingDir, "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));
        FileTasks.replaceTokensInFolder(bindingMetaDir, "PARTNER_IP_AND_PORT", Configuration.get("partner.ipAndPort"));

        ZipTasks.zipFolder(getBindingArchive(), bindingDir);
    }

    private Path getBindingArchive() {
        return process.getTargetTmpPath().resolve(process.getName() + "Binding.zip");
    }

}

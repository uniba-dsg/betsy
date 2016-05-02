package betsy.bpel.engines.openesb;

import betsy.bpel.model.BPELProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.tasks.XSLTTasks;
import betsy.common.tasks.ZipTasks;

import java.nio.file.Path;
import java.util.Objects;

public class OpenEsbCompositePackager {

    private final BPELProcess process;

    public OpenEsbCompositePackager(BPELProcess process) {
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
        XSLTTasks.transform(process.getEngine().getXsltPath().resolve("create_composite_jbi_from_bpel.xsl"), process.getTargetProcessFilePath(), compositeMetaDir.resolve("jbi.xml"));
        XSLTTasks.transform(process.getEngine().getXsltPath().resolve("create_composite_manifest_from_bpel.xsl"), process.getTargetProcessFilePath(), compositeMetaDir.resolve("MANIFEST.MF"));
        FileTasks.copyFileIntoFolder(process.getTargetPackageJarFilePath(), compositeDir);
        FileTasks.copyFileIntoFolder(getBindingArchive(), compositeDir);

        // build composite
        ZipTasks.zipFolder(process.getTargetPackageCompositeFilePath(), compositeDir);
    }

    private void createBinding() {
        // create jar file
        FileTasks.move(process.getTargetPackageFilePath(), process.getTargetPackageJarFilePath());

        // create http binding
        Path bindingDir = process.getTargetTmpPath().resolve("binding");
        FileTasks.mkdirs(bindingDir);

        Path bindingMetaDir = bindingDir.resolve("META-INF");
        FileTasks.mkdirs(bindingMetaDir);

        XSLTTasks.transform(process.getEngine().getXsltPath().resolve("create_binding_jbi_from_bpel.xsl"), process.getTargetProcessFilePath(), bindingMetaDir.resolve("jbi.xml"));
        Path catalogFile = bindingMetaDir.resolve("catalog.xml");

        FileTasks.createFile(catalogFile, "<?xml version='1.0' encoding='UTF-8' standalone='no'?><catalog xmlns='urn:oasis:names:tc:entity:xmlns:xml:catalog' prefer='system'></catalog>");
        FileTasks.createFile(bindingMetaDir.resolve("MANIFEST.MF"), "Manifest-Version: 1.0");

        FileTasks.copyMatchingFilesIntoFolder(process.getTargetProcessPath(), bindingDir.resolve(process.getName()), "*.xsd");
        FileTasks.copyMatchingFilesIntoFolder(process.getTargetProcessPath(), bindingDir.resolve(process.getName()), "*.wsdl");

        ZipTasks.zipFolder(getBindingArchive(), bindingDir);
    }

    private Path getBindingArchive() {
        return process.getTargetTmpPath().resolve("sun-http-binding.jar");
    }

}

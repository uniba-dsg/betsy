package betsy.data.engines.openesb

import ant.tasks.AntUtil
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks

import java.nio.file.Path

class OpenEsbCompositePackager {

    final AntBuilder ant = AntUtil.builder()
    BetsyProcess process

    void build() {
        createBinding()
        createComposite()
    }

    private void createComposite() {
        // create composite
        Path compositeDir = process.targetTmpPath.resolve("composite")
        Path compositeMetaDir = compositeDir.resolve("META-INF")
        FileTasks.mkdirs(compositeMetaDir)
        ant.xslt(in: process.targetBpelFilePath, out: compositeMetaDir.resolve("jbi.xml"), style: process.engine.xsltPath.resolve("create_composite_jbi_from_bpel.xsl"))
        ant.xslt(in: process.targetBpelFilePath, out: compositeMetaDir.resolve("MANIFEST.MF"), style: process.engine.xsltPath.resolve("create_composite_manifest_from_bpel.xsl"))
        ant.copy file: process.targetPackageJarFilePath, todir: compositeDir
        ant.copy file: bindingArchive, todir: compositeDir

        // build composite
        ant.zip file: process.targetPackageCompositeFilePath, basedir: compositeDir
    }

    private void createBinding() {
        // create jar file
        ant.move file: process.targetPackageFilePath, tofile: process.targetPackageJarFilePath

        // create http binding
        Path bindingDir = process.targetTmpPath.resolve("binding")
        FileTasks.mkdirs(bindingDir)

        Path bindingMetaDir = bindingDir.resolve("META-INF")
        FileTasks.mkdirs(bindingMetaDir)

        ant.xslt(in: process.targetBpelFilePath, out: bindingMetaDir.resolve("jbi.xml"), style: process.engine.xsltPath.resolve("create_binding_jbi_from_bpel.xsl"))
        Path catalogFile = bindingMetaDir.resolve("catalog.xml")

        ant.echo file: catalogFile, message: """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog" prefer="system">
</catalog>
        """
        ant.echo file: bindingMetaDir.resolve("MANIFEST.MF"), message: "Manifest-Version: 1.0"

        ant.copy(todir: bindingDir.resolve(process.name)) {
            fileset(dir: process.targetBpelPath, includes: "*.xsd")
            fileset(dir: process.targetBpelPath, includes: "*.wsdl")
        }

        ant.zip(file: bindingArchive, basedir: bindingDir)
    }

    private Path getBindingArchive() {
        process.targetTmpPath.resolve("sun-http-binding.jar")
    }
}

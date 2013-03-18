package betsy.data.engines.packager

import betsy.data.Process

class OpenEsbCompositePackager {

    AntBuilder ant = new AntBuilder()
    Process process

    void build() {
        createBinding()
        createComposite()
    }

    private void createComposite() {
        // create composite
        String compositeDir = "${process.targetPath}/composite"
        String compositeMetaDir = "$compositeDir/META-INF"
        ant.mkdir dir: compositeMetaDir
        ant.xslt(in: process.targetBpelFilePath, out: "$compositeMetaDir/jbi.xml", style: "${process.engine.xsltPath}/create_composite_jbi_from_bpel.xsl")
        ant.xslt(in: process.targetBpelFilePath, out: "$compositeMetaDir/MANIFEST.MF", style: "${process.engine.xsltPath}/create_composite_manifest_from_bpel.xsl")
        ant.copy file: process.targetPackageJarFilePath, todir: compositeDir
        ant.copy file: bindingArchive, todir: compositeDir

        // build composite
        ant.zip file: process.targetPackageCompositeFilePath, basedir: compositeDir
    }

    private void createBinding() {
        // create jar file
        ant.copy file: process.targetPackageFilePath, tofile: process.targetPackageJarFilePath

        // create http binding
        String bindingDir = "${process.targetPath}/binding"
        String bindingMetaDir = "$bindingDir/META-INF"
        ant.mkdir dir: bindingDir
        ant.xslt(in: process.targetBpelFilePath, out: "$bindingMetaDir/jbi.xml", style: "${process.engine.xsltPath}/create_binding_jbi_from_bpel.xsl")
        String catalogFile = "$bindingMetaDir/catalog.xml"
        ant.mkdir(dir: bindingMetaDir)
        ant.echo file: catalogFile, message: """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog" prefer="system">
</catalog>
        """
        ant.echo file: "$bindingMetaDir/MANIFEST.MF", message: "Manifest-Version: 1.0"

        ant.copy(todir: "$bindingDir/${process.bpelFileNameWithoutExtension}") {
            fileset(dir: process.targetBpelPath, includes: "*.xsd")
            fileset(dir: process.targetBpelPath, includes: "*.wsdl")
        }

        ant.zip(file: bindingArchive, basedir: bindingDir)
    }

    private String getBindingArchive() {
        "${process.targetPackagePath}/sun-http-binding.jar"
    }
}

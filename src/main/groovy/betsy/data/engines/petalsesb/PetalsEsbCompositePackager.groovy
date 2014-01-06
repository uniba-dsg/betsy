package betsy.data.engines.petalsesb

import ant.tasks.AntUtil
import betsy.config.Configuration;
import betsy.data.BetsyProcess
import betsy.tasks.FileTasks

import java.nio.file.Path

class PetalsEsbCompositePackager {

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
        ant.move file: process.targetPackageFilePath, todir: compositeDir
        ant.copy file: bindingArchive, todir: compositeDir

        ant.replace(dir: compositeDir, token: "PARTNER_IP_AND_PORT", value: Configuration.get("partner.ipAndPort"))
        ant.replace(dir: compositeMetaDir, token: "PARTNER_IP_AND_PORT", value: Configuration.get("partner.ipAndPort"))

        // build composite
        ant.zip file: process.targetPackageCompositeFilePath, basedir: compositeDir
    }

    void createBinding() {
        Path bindingDir = process.targetTmpPath.resolve("binding")
        Path bindingMetaDir = bindingDir.resolve("META-INF")
        FileTasks.mkdirs(bindingDir)
        FileTasks.mkdirs(bindingMetaDir)
        ant.xslt(in: process.targetBpelFilePath, out: bindingMetaDir.resolve("jbi.xml"), style: process.engine.xsltPath.resolve("create_binding_jbi_from_bpel.xsl"))
        ant.copy(todir: bindingDir) {
            fileset(dir: process.targetBpelPath, includes: "*.xsd")
            fileset(dir: process.targetBpelPath, includes: "*.wsdl")
        }

        ant.replace(dir: bindingDir, token: "PARTNER_IP_AND_PORT", value: Configuration.get("partner.ipAndPort"))
        ant.replace(dir: bindingMetaDir, token: "PARTNER_IP_AND_PORT", value: Configuration.get("partner.ipAndPort"))

        ant.zip(file: bindingArchive, basedir: bindingDir)
    }

    private Path getBindingArchive() {
        process.targetTmpPath.resolve("${process.name}Binding.zip")
    }

}

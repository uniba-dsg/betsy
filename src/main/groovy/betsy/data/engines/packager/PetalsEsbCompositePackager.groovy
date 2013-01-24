package betsy.data.engines.packager

import betsy.Configuration
import betsy.data.Process

class PetalsEsbCompositePackager {

    AntBuilder ant = new AntBuilder()
    Process process

    private String sun_http_binding

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
        ant.copy file: process.targetPackageFilePath, todir: compositeDir
        ant.copy file: sun_http_binding, todir: compositeDir

        ant.replace(dir: compositeDir, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)
        ant.replace(dir: compositeMetaDir, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)

        // build composite
        ant.zip file: process.targetPackageCompositeFilePath, basedir: compositeDir
    }

    void createBinding() {
        String bindingDir = "${process.targetPath}/binding"
        String bindingMetaDir = "$bindingDir/META-INF"
        ant.mkdir dir: bindingDir
        ant.mkdir(dir: bindingMetaDir)
        ant.xslt(in: process.targetBpelFilePath, out: "$bindingMetaDir/jbi.xml", style: "${process.engine.xsltPath}/create_binding_jbi_from_bpel.xsl")
        ant.copy(todir: bindingDir) {
            fileset(dir: process.targetBpelPath, includes: "*.xsd")
            fileset(dir: process.targetBpelPath, includes: "*.wsdl")
        }

        ant.replace(dir: bindingDir, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)
        ant.replace(dir: bindingMetaDir, token: "PARTNER_IP_AND_PORT", value: Configuration.PARTNER_IP_AND_PORT)

        sun_http_binding = "${process.targetPackagePath}/${process.bpelFileNameWithoutExtension}Binding.zip"
        ant.zip(file: sun_http_binding, basedir: bindingDir)
    }

}

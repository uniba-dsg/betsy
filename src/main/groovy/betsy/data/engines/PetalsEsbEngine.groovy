package betsy.data.engines

import betsy.data.Engine
import betsy.data.Process
import betsy.data.engines.packager.PetalsEsbCompositePackager

import java.nio.file.Files
import java.nio.file.Paths

class PetalsEsbEngine extends Engine {

    private static final String CHECK_URL = "http://localhost:8084"

    @Override
    String getName() {
        "petalsesb"
    }

    @Override
    String getEndpointUrl(Process process) {
        "$CHECK_URL/petals/services/${process.bpelFileNameWithoutExtension}TestInterfaceService"
    }

    @Override
    void startup() {
        ant.parallel() {
            ant.exec(executable: "cmd", failOnError: "true", dir: "${getServerPath()}/petals-esb-4.0/bin") {
                arg(value: "/c")
                arg(value: "petals-esb.bat")
            }
            waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
                resourcecontains(resource: "${getServerPath()}/petals-esb-4.0/logs/petals.log", substring: "[Petals.Container.Components.petals-se-bpel] : Component started")
            }
        }
    }

    @Override
    void shutdown() {
        ant.exec(executable: "cmd") {
            arg(value: "/c")
            arg(value: "taskkill")
            arg(value: '/FI')
            arg(value: "WINDOWTITLE eq OW2*")
        }
    }

    @Override
    void install() {
        ant.ant(antfile: "build.xml", target: getName())
    }

    @Override
    void deploy(Process process) {
        String installationDir = "${getServerPath()}/petals-esb-4.0/install"

        ant.copy(file: process.targetPackageCompositeFilePath, todir: installationDir)
    }

    @Override
    void onPostDeployment() {
        ant.parallel() {
            processes.each { process ->
                onPostDeployment(process)
            }
        }
    }

    @Override
    void onPostDeployment(Process process) {
        String installationDir = "${getServerPath()}/petals-esb-4.0/install"
        ant.waitfor(maxwait: "100", maxwaitunit: "second", checkevery: "1000") {
            not() {
                available(file: "$installationDir/${process.targetPackageCompositeFile}")
            }
        }
    }

    @Override
    void buildDeploymentDescriptor(Process process) {
        String metaDir = "${process.targetBpelPath}/META-INF"
        ant.mkdir(dir: metaDir)
        ant.xslt(in: process.targetBpelFilePath, out: "$metaDir/jbi.xml", style: "$xsltPath/create_jbi_from_bpel.xsl")
    }

    @Override
    void buildAdditionalArchives(Process process) {
        new PetalsEsbCompositePackager(process: process, ant: ant).build()
    }

    @Override
    void transform(Process process) {
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

        if (Files.exists(Paths.get("${process.targetBpelPath}/TestPartner.wsdl"))) {
            ant.replace(file: "${process.targetBpelPath}/TestPartner.wsdl", token: "TestService", value: "${process.bpelFileNameWithoutExtension}TestService")
        }

    }

    @Override
    void failIfRunning() {
        ant.fail(message: "server for engine ${this} is still running") {
            condition() {
                http url: CHECK_URL
            }
        }
    }

}

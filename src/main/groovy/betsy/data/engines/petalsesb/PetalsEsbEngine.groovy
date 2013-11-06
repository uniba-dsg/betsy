package betsy.data.engines.petalsesb

import betsy.data.BetsyProcess
import betsy.data.Engine

import java.nio.file.Files
import java.nio.file.Paths

class PetalsEsbEngine extends Engine {

    public static final String CHECK_URL = "http://localhost:8084"

    @Override
    String getName() {
        "petalsesb"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "$CHECK_URL/petals/services/${process.bpelFileNameWithoutExtension}TestInterfaceService"
    }

    String getFolder() {
        "petals-esb-4.0"
    }

    String getPetalsLog() {
        "${getServerPath()}/${getFolder()}/logs/petals.log"
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(file: getPetalsLog(), todir: "${process.targetPath}/logs")
    }

    @Override
    void startup() {
        ant.exec(executable: "cmd", failOnError: "true", dir: "${getServerPath()}/${getFolder()}/bin") {
            arg(value: "/c")
            arg(value: "petals-esb.bat")
        }
        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "500") {
            and {
                resourcecontains(resource: "${getServerPath()}/${getFolder()}/logs/petals.log",
                        substring: "[Petals.Container.Components.petals-bc-soap] : Component started")
                resourcecontains(resource: "${getServerPath()}/${getFolder()}/logs/petals.log",
                        substring: "[Petals.Container.Components.petals-se-bpel] : Component started")
            }
        }

        try {
            ant.fail(message: "SOAP BC not installed correctly") {
                condition() {
                    resourcecontains(resource: "${getServerPath()}/${getFolder()}/logs/petals.log",
                            substring: "[Petals.AutoLoaderService] : Error during the auto- installation of a component")
                }
            }
        } catch (Exception ignore) {
            ant.echo message: "SOAP BC Installation failed - shutdown, reinstall and start petalsesb again"
            shutdown()
            install()
            startup()
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
        new PetalsEsbInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        ant.copy(file: process.targetPackageCompositeFilePath, todir: installationDir)

        ant.waitfor(maxwait: "30", maxwaitunit: "second", checkevery: "1000") {
            and {
                not() { available(file: "$installationDir/${process.targetPackageCompositeFile}") }
                or {
                    resourcecontains(resource: getPetalsLog(),
                            substring: "Service Assembly '${process.getBpelFileNameWithoutExtension()}Application' started")
                    resourcecontains(resource: getPetalsLog(),
                            substring: "Service Assembly '${process.getBpelFileNameWithoutExtension()}Application' deployed with some SU deployment in failure")
                }
            }
        }
    }

    String getInstallationDir() {
        "${getServerPath()}/${getFolder()}/install"
    }

    @Override
    void buildArchives(BetsyProcess process) {
        packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        String metaDir = "${process.targetBpelPath}/META-INF"
        ant.mkdir(dir: metaDir)
        ant.xslt(in: process.targetBpelFilePath, out: "$metaDir/jbi.xml", style: "$xsltPath/create_jbi_from_bpel.xsl")

        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService",
                value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

        if (Files.exists(Paths.get("${process.targetBpelPath}/TestPartner.wsdl"))) {
            ant.replace(file: "${process.targetBpelPath}/TestPartner.wsdl", token: "TestService",
                    value: "${process.bpelFileNameWithoutExtension}TestService")
        }

        packageBuilder.replaceEndpointTokenWithValue(process)
        packageBuilder.replacePartnerTokenWithValue(process)
        packageBuilder.bpelFolderToZipFile(process)

        new PetalsEsbCompositePackager(process: process, ant: ant).build()
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

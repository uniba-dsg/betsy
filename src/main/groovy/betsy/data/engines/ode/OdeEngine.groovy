package betsy.data.engines.ode

import betsy.data.Engine
import betsy.data.BetsyProcess
import betsy.data.engines.tomcat.Tomcat

class OdeEngine extends Engine {

    @Override
    String getName() {
        "ode"
    }

    @Override
    String getEndpointUrl(BetsyProcess process) {
        "${tomcat.tomcatUrl}/ode/processes/${process.bpelFileNameWithoutExtension}TestInterface"
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/webapps/ode/WEB-INF/processes"
    }

    Tomcat getTomcat() {
        new Tomcat(ant: ant, engineDir: serverPath)
    }

    @Override
    void startup() {
        tomcat.startup()
    }

    @Override
    void shutdown() {
        tomcat.shutdown()
    }

    @Override
    void storeLogs(BetsyProcess process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void install() {
        new OdeInstaller().install()
    }

    @Override
    void deploy(BetsyProcess process) {
        ant.unzip src: process.targetPackageFilePath, dest: "$deploymentDir/${process.bpelFileNameWithoutExtension}"
    }

    @Override
    void onPostDeployment(BetsyProcess process) {
        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                and {
                    available file: "$deploymentDir/${process.bpelFileNameWithoutExtension}.deployed"
                    or {
                        resourcecontains(resource: "${tomcat.tomcatDir}/logs/ode.log", substring: "Deployment of artifact " + process.bpelFileNameWithoutExtension + " successful")
                        resourcecontains(resource: "${tomcat.tomcatDir}/logs/ode.log", substring: "Deployment of " + process.bpelFileNameWithoutExtension + " failed")
                    }
                }
            }
        }
    }

    @Override
    void buildArchives(BetsyProcess process) {
        createFolderAndCopyProcessFilesToTarget(process)

        // engine specific steps
        ant.xslt(in: process.bpelFilePath, out: "${process.targetBpelPath}/deploy.xml", style: "$xsltPath/bpel_to_ode_deploy_xml.xsl")
        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")
        ant.replace(file: "${process.targetBpelPath}/deploy.xml", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

        replaceEndpointAndPartnerTokensWithValues(process)

        bpelFolderToZipFile(process)
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

}

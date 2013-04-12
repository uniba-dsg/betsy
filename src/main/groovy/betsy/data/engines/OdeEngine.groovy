package betsy.data.engines

import betsy.data.Engine
import betsy.data.Process
import betsy.data.engines.server.Tomcat

class OdeEngine extends Engine {

    @Override
    String getName() {
        "ode"
    }

    @Override
    String getEndpointUrl(Process process) {
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
    void storeLogs(Process process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void install() {
        ant.ant(antfile: "build.xml", target: getName())
    }

    @Override
    void deploy(Process process) {
        ant.unzip src: process.targetPackageFilePath, dest: "$deploymentDir/${process.bpelFileNameWithoutExtension}"
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
        ant.sequential() {
            ant.waitfor(maxwait: "30", maxwaitunit: "second") {
                and {
                    available file: "$deploymentDir/${process.bpelFileNameWithoutExtension}.deployed"
                    or {
                        resourcecontains(resource: "${tomcat.tomcatDir}/logs/ode.log", substring: "Deployment of artifact ${process.bpelFileNameWithoutExtension} successful")
                        resourcecontains(resource: "${tomcat.tomcatDir}/logs/ode.log", substring: "Deployment of ${process.bpelFileNameWithoutExtension} failed")
                    }
                }
            }
        }
    }

    @Override
    void buildArchives(Process process) {
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

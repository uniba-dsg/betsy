package betsy.data.engines

import betsy.data.Engine

import betsy.data.Process
import betsy.data.engines.server.Tomcat
import betsy.data.WsdlOperation

class BpelgEngine extends Engine {

    @Override
    String getName() {
        "bpelg"
    }

    @Override
    String getDeploymentPrefix() {
        "${tomcat.tomcatUrl}/bpel-g/services"
    }

    String getDeploymentDir() {
        "${tomcat.tomcatDir}/bpr"
    }

    @Override
    void storeLogs(Process process) {
        ant.mkdir(dir: "${process.targetPath}/logs")
        ant.copy(todir: "${process.targetPath}/logs") {
            ant.fileset(dir: "${tomcat.tomcatDir}/logs/")
        }
    }

    @Override
    void failIfRunning() {
        tomcat.checkIfIsRunning()
    }

    @Override
    String getDeploymentPostfix() {
        "TestInterfaceService"
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
    void install() {
         ant.ant(antfile: "build.xml", target: getName())
    }

    @Override
    void deploy(Process process) {
        ant.copy(file: process.targetPackageFilePath, todir: deploymentDir)
    }

    @Override
    void buildDeploymentDescriptor(Process process) {
        ant.xslt(in: process.bpelFilePath, out: "${process.targetBpelPath}/deploy.xml", style: "${getXsltPath()}/bpelg_bpel_to_deploy_xml.xsl")
    }

    @Override
    void onPostDeployment() {
        ant.echo(message: "waiting for the bpel-g deployment process to fire")

        ant.parallel() {
            processes.each { process ->
                onPostDeployment(process)
            }
        }
    }

    @Override
    void onPostDeployment(Process process) {
        ant.sequential() {
            ant.waitfor(maxwait: "100", maxwaitunit: "second") {
                available file: "${deploymentDir}/work/ae_temp_${process.bpelFileNameWithoutExtension}_zip/deploy.xml"
            }
            ant.sleep(milliseconds: 1000)
        }
    }

    @Override
    void transform(Process process) {
        ant.xslt(in: "${process.PATH_PREFIX}/language-features/TestInterface.wsdl", out: "${process.targetBpelPath}/TestInterface.wsdl", style: "$xsltPath/bpelg_prepare_wsdl.xsl", force: "yes") {
            param(name: "deletePattern", expression: computeMatchingPattern(process))
        }

        ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")
        ant.replace(file: "${process.targetBpelPath}/deploy.xml", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")
    }

    private String computeMatchingPattern(Process process) {
        // This method works based on the knowledge that we have no more than two operations available anyway
        String text = new File(process.bpelFilePath).getText()

        if (!text.contains(WsdlOperation.SYNC.name)) {
            return WsdlOperation.SYNC.name
        } else if (!text.contains(WsdlOperation.ASYNC.name)) {
            return WsdlOperation.ASYNC.name
        } else {
            return ""
        }
    }

}

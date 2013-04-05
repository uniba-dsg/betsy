package betsy.data.engines.bpelg


import betsy.data.Process
import betsy.data.engines.EnginePackageBuilder;
import betsy.data.engines.LocalEngine;
import betsy.data.engines.Tomcat;
import betsy.data.engines.Util;

class BpelgEngine extends LocalEngine {

	@Override
	String getName() {
		"bpelg"
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
	void onPostDeployment() {
		ant.echo(message: "waiting for the bpel-g deployment process to fire")

		ant.parallel() {
			processes.each { process -> onPostDeployment(process) }
		}
	}

	@Override
	void onPostDeployment(Process process) {
		ant.sequential() {
			ant.waitfor(maxwait: "100", maxwaitunit: "second") { available file: "${deploymentDir}/work/ae_temp_${process.bpelFileNameWithoutExtension}_zip/deploy.xml" }
			ant.sleep(milliseconds: 1000)
		}
	}

	@Override
	void buildArchives(Process process) {
		packageBuilder.createFolderAndCopyProcessFilesToTarget(process)

		// deployment descriptor
		ant.xslt(in: process.bpelFilePath, out: "${process.targetBpelPath}/deploy.xml", style: "${getXsltPath()}/bpelg_bpel_to_deploy_xml.xsl")

		// remove unimplemented methods
		ant.xslt(in: "${process.PATH_PREFIX}/language-features/TestInterface.wsdl", out: "${process.targetBpelPath}/TestInterface.wsdl", style: "$xsltPath/bpelg_prepare_wsdl.xsl", force: "yes") {
			param(name: "deletePattern", expression: Util.computeMatchingPattern(process))
		}
		// uniquify service name
		ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")
		ant.replace(file: "${process.targetBpelPath}/deploy.xml", token: "TestInterfaceService", value: "${process.bpelFileNameWithoutExtension}TestInterfaceService")

		packageBuilder.replaceEndpointTokenWithValue(process)
		packageBuilder.replacePartnerTokenWithValue(process)
		packageBuilder.bpelFolderToZipFile(process)
	}

	@Override
	String getEndpointUrl(Process process) {
		"${tomcat.tomcatUrl}/bpel-g/services/${process.bpelFileNameWithoutExtension}TestInterfaceService"
	}

}

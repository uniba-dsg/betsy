package betsy.data;

import de.uniba.wiai.dsg.betsy.Configuration;
import groovy.util.AntBuilder;

public class EnginePackageBuilder {

	private final String engineName
	private final AntBuilder ant = new AntBuilder()
	private final Configuration config = Configuration.getInstance();

	public EnginePackageBuilder(final String engineName) {
		if(engineName == null || engineName.trim().isEmpty()) {
			throw new IllegalArgumentException("engineName must not be null " +
			"or empty");
		}
		this.engineName = engineName;
	}

	public void createFolderAndCopyProcessFilesToTarget(Process process) {
		// engine independent package steps
		ant.mkdir dir: process.targetPath

		ant.echo message: "Copying files for ${process} for engine ${engineName}"

		ant.copy file: process.bpelFilePath, todir: process.targetBpelPath
		ant.replace(file: process.targetBpelFilePath, token: "../", value: "")

		process.wsdlPaths.each { wsdlPath ->
			ant.copy file: wsdlPath, todir: process.targetBpelPath
		}

		process.additionalFilePaths.each {additionalPath ->
			ant.copy file: additionalPath, todir: process.targetBpelPath
		}
	}

	public void bpelFolderToZipFile(Process process) {
		ant.mkdir dir: process.targetPackagePath
		ant.zip file: process.targetPackageFilePath, basedir: process.targetBpelPath
	}

	// TODO include into engine
	public void replaceEndpointTokenWithValue(Process process) {
		ant.echo message: "Setting Endpoint of wsdl IF for $process on ${engineName} to ${process.endpoint}"
		ant.replace(file: "${process.targetBpelPath}/TestInterface.wsdl", token: "ENDPOINT_URL", value: process.endpoint)
	}
	
	// TODO include into engine
	public void replacePartnerTokenWithValue(Process process) {
		ant.echo message: "Setting Partner Address of for $process on ${engineName} to ${config.getValueAsString('PARTNER_IP_AND_PORT')}"
		ant.replace(dir: process.targetBpelPath, token: "PARTNER_IP_AND_PORT", value: config.getValueAsString('PARTNER_IP_AND_PORT'))
	}

}

package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedOrchestraDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "orchestra_v";
	}

	@Override
	public void deploy(DeployOperation container) throws DeployException {
		// write data to temp dir first
		File tmpFile = new File("tmp", container.getFilename());
		createLocalDeployFile(tmpFile, container);
		log.info("Temporary deploy file written to disk");

		// ant must have been installed for the installation itself
		File buildFile = new File(container.getDeploymentDir(), "build.xml");
		if (!buildFile.exists()) {
			log.error("Deployment failed: The build.xml file "
					+ "of orchestra has not been found at '"
					+ buildFile.getAbsolutePath() + "'");
			throw new DeployException("Deployment failed: The build.xml file "
					+ "of orchestra has not been found at '"
					+ buildFile.getAbsolutePath() + "'");
		}

		Runtime runtime = Runtime.getRuntime();
		String[] attributes = { "ant", "-f", buildFile.getAbsolutePath(),
				"deploy", "-Dbar=" + tmpFile.getAbsolutePath() };

		log.debug("Executing now:");
		log.debug(attributes.toString());

		try {
			Process proc = runtime.exec(attributes);
			InputStream inStr = proc.getInputStream();
			BufferedReader buff = new BufferedReader(new InputStreamReader(
					inStr));
			String str;
			log.debug("Console output:");
			while ((str = buff.readLine()) != null) {
				log.debug("--:" + str);
			}
		} catch (IOException exception) {
			log.error("Deploying failed:", exception);
			throw new DeployException("Deployment failed because of an "
					+ "unexpected exception:", exception);
		}
	}

	private void createLocalDeployFile(File tmpFile, DeployOperation container)
			throws DeployException {
		try {
			FileUtils.writeByteArrayToFile(tmpFile, container.getData());
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

	@Override
	public void onPostDeployment(DeployOperation container)
			throws DeployException {
		// do nothing - as using synchronous deployment
	}

}

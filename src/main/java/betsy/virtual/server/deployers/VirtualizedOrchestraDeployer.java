package betsy.virtual.server.deployers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;

/**
 * Deployer for the virtualized Orchestra engine.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedOrchestraDeployer implements VirtualizedEngineDeployer {

	private static final Logger log = Logger
			.getLogger(VirtualizedOrchestraDeployer.class);

	@Override
	public String getName() {
		return "orchestra_v";
	}

	@Override
	public void deploy(DeployOperation operation) throws DeployException {
		// write data to temp dir first
		File tmpFile = new File("tmp", operation.getFileMessage().getFilename());
		createLocalDeployFile(tmpFile, operation);
		log.info("Temporary deploy file written to disk");

		// ant must have been installed for the installation itself
		File buildFile = new File(operation.getDeploymentDir(), "build.xml");
		if (!buildFile.exists()) {
			throw new DeployException("Deployment failed: The build.xml file "
					+ "of orchestra has not been found at '"
					+ buildFile.getAbsolutePath() + "'");
		}

		Runtime runtime = Runtime.getRuntime();
		String[] attributes = { "ant", "-f", buildFile.getAbsolutePath(),
				"deploy", "-Dbar=" + tmpFile.getAbsolutePath() };

		log.debug("Executing now:");
		log.debug(Arrays.toString(attributes));

		BufferedReader buff = null;
		try {
			Process proc = runtime.exec(attributes);
			InputStream inStr = proc.getInputStream();
			buff = new BufferedReader(new InputStreamReader(inStr));
			String str;
			log.debug("Console output:");
			while ((str = buff.readLine()) != null) {
				log.debug("--:" + str);
			}
		} catch (IOException exception) {
			if (buff != null) {
				try {
					buff.close();
				} catch (IOException e) {
					// ignore
				}
			}
			throw new DeployException("Deployment failed because of an "
					+ "unexpected exception:", exception);
		}
	}

	private void createLocalDeployFile(File tmpFile, DeployOperation container)
			throws DeployException {
		try {
			FileUtils.writeByteArrayToFile(tmpFile, container.getFileMessage()
					.getData());
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

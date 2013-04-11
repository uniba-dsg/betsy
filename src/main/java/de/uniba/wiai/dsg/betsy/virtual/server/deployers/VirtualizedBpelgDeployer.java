package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

/**
 * Deployer for the virtualized BPEL-g engine.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedBpelgDeployer implements VirtualizedEngineDeployer {

	private static final Logger log = Logger.getLogger(VirtualizedBpelgDeployer.class);

	@Override
	public String getName() {
		return "bpelg_v";
	}

	@Override
	public void onPostDeployment(DeployOperation container)
			throws DeployException {
		log.info("waiting for the bpel-g_v deployment process to fire");

		boolean fileAvailable = isDeployedFileAvailable(container);
		long start = -System.currentTimeMillis();
		int deployTimeout = container.getDeployTimeout();

		while (!fileAvailable
				&& (System.currentTimeMillis() + start < deployTimeout)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
			fileAvailable = isDeployedFileAvailable(container);
		}

		if (!fileAvailable) {
			// timed out :/
			throw new DeployException("Process could not be deployed within "
					+ deployTimeout + "seconds. The operation timed out.");
		}

		// process is deployed, now wait for verification in logfile
		boolean logVerification = false;
		File catalinaLog = new File(container.getEngineLogfileDir(),
				"catalina.out");
		String successMessage = "Deployment successful";
		String errorMessage = "Deployment failed";
		int errorCount = 0;

		if (catalinaLog.isFile()) {
			// verify deployment with engine log. Either until deployment
			// result or until timeout is reached
			while (!logVerification
					&& (System.currentTimeMillis() + start < deployTimeout)) {
				log.debug("try log verification...");
				try {
					String fileContent = FileUtils
							.readFileToString(catalinaLog);
					// try positive case
					logVerification = fileContent.contains(successMessage);
					// try negative case
					if (!logVerification) {
						logVerification = fileContent.contains(errorMessage);
					}
					if (!logVerification) {
						// not available yet? wait a little...
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// ignore
						}
					}
				} catch (IOException exception) {
					log.error("Error while reading catalina.out:", exception);
					if (errorCount > 3) {
						log.error("Reading catalina.out failed several times"
								+ ", skip log verification");
					}
				}
			}
		} else {
			log.warn("Catalina.out not found, wait 1s for deployment");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private boolean isDeployedFileAvailable(DeployOperation container) {
		File file = new File(container.getDeploymentDir() + "/work/ae_temp_"
				+ container.getBpelFileNameWithoutExtension()
				+ "_zip/deploy.xml");
		return file.isFile();
	}

	@Override
	public void deploy(DeployOperation container) throws DeployException {
		try {
			File file = new File(container.getDeploymentDir(), container
					.getFileMessage().getFilename());
			FileUtils.writeByteArrayToFile(file, container.getFileMessage()
					.getData());
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

}

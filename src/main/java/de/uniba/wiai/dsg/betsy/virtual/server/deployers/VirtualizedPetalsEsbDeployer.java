package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

/**
 * Deployer for the virtualized PetalsESB engine.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedPetalsEsbDeployer implements VirtualizedEngineDeployer {

	private static final Logger log = Logger.getLogger(VirtualizedPetalsEsbDeployer.class);

	@Override
	public String getName() {
		return "petalsesb_v";
	}

	private File getDeployDestination(DeployOperation container) {
		return new File(container.getDeploymentDir(), container
				.getFileMessage().getFilename());
	}

	private boolean isDeployedFileAvailable(DeployOperation container) {
		File file = getDeployDestination(container);
		return file.isFile();
	}

	@Override
	public void deploy(DeployOperation container) throws DeployException {
		try {
			File file = getDeployDestination(container);
			FileUtils.writeByteArrayToFile(file, container.getFileMessage()
					.getData());
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

	@Override
	public void onPostDeployment(DeployOperation container)
			throws DeployException {
		log.info("waiting for the petalsesb_v deployment process to fire");

		boolean fileAvailable = isDeployedFileAvailable(container);
		long start = -System.currentTimeMillis();
		int deployTimeout = container.getDeployTimeout();

		// Be careful: here the process is deployed if the file is missing!
		while (fileAvailable
				&& (System.currentTimeMillis() + start < deployTimeout)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
			fileAvailable = isDeployedFileAvailable(container);
		}

		if (fileAvailable) {
			// timed out :/
			throw new DeployException("Process could not be deployed within "
					+ deployTimeout + "seconds. The operation timed out.");
		}

		log.debug("File deployment done");

		// process is deployed, now wait for verification in logfile
		boolean logVerification = false;
		File logfile = new File(container.getEngineLogfileDir(), "petals.log");
		String successMessage = "Service Assembly '"
				+ container.getBpelFileNameWithoutExtension()
				+ "Application' started";
		int errorCount = 0;

		if (logfile.isFile()) {
			// verify deployment with engine log. Either until deployment
			// result or until timeout is reached
			while (!logVerification
					&& (System.currentTimeMillis() + start < deployTimeout)) {
				log.debug("try log verification...");
				try {
					String fileContent = FileUtils.readFileToString(logfile);
					// try positive case
					logVerification = fileContent.contains(successMessage);

					if (!logVerification) {
						// not available yet? wait a little...
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// ignore
						}
					}
				} catch (IOException exception) {
					log.error("Error while reading petals.log:", exception);
					if (errorCount > 3) {
						log.error("Reading petals.log failed several times"
								+ ", skip log verification");
					}
				}
			}
			log.trace("Timeout? "
					+ (System.currentTimeMillis() + start > deployTimeout));
			log.trace("Log verification? " + logVerification);
		} else {
			log.warn("petals.log not found, skip log verification");
		}
	}
}

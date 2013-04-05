package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedActiveBpelDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "active_bpel_v";
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

	@Override
	public void onPostDeployment(DeployOperation container)
			throws DeployException {
		log.info("waiting for the active_bpel_v deployment process to fire");

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

		log.info("Deploy catalog found");

		// process is deployed, now wait for verification in logfile
		boolean logVerification = false;
		File catalinaLog = new File(container.getEngineLogfileDir(),
				"aeDeployment.log");
		String successMessage = "["
				+ container.getBpelFileNameWithoutExtension() + ".pdd]";
		int errorCount = 0;

		if (catalinaLog.isFile()) {
			// verify deployment with engine log. Either until deployment
			// result or until timeout is reached
			while (!logVerification
					&& (System.currentTimeMillis() + start < deployTimeout)) {
				log.debug("try log verification, wait for: '" + successMessage
						+ "'");
				try {
					String fileContent = FileUtils
							.readFileToString(catalinaLog);
					logVerification = fileContent.contains(successMessage);
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
			if (!logVerification) {
				throw new DeployException("Process could not be deployed "
						+ "within " + deployTimeout + "seconds. Log "
						+ "verification failed. The operation timed out.");
			}
		} else {
			log.warn("aeDeployment.log not found, wait 1s for deployment");
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
				+ "_bpr/META-INF/catalog.xml");
		return file.isFile();
	}
}

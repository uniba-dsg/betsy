package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedBpelgDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "bpelg_v";
	}

	@Override
	public void onPostDeployment(DeployContainer container)
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
		boolean secondVerification = false;
		File catalinaLog = new File(container.getEngineLogfileDir(),
				"catalina.out");
		String successMessage = "Deployment successful";
		String errorMessage = "Deployment failed";
		int errorCount = 0;

		if (catalinaLog.isFile()) {
			// verify deployment with engine log. Either until deployment
			// result or until timeout is reached
			while (!secondVerification
					&& (System.currentTimeMillis() + start < deployTimeout)) {
				log.debug("try log verification...");
				try {
					String fileContent = FileUtils
							.readFileToString(catalinaLog);
					// try positive case
					secondVerification = fileContent.contains(successMessage);
					// try negative case
					if (!secondVerification) {
						secondVerification = fileContent.contains(errorMessage);
					}
					if (!secondVerification) {
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

	private boolean isDeployedFileAvailable(DeployContainer container) {
		File file = new File(container.getDeploymentDir() + "/work/ae_temp_"
				+ container.getBpelFileNameWithoutExtension()
				+ "_zip/deploy.xml");
		return file.isFile();
	}

	@Override
	public void deploy(DeployContainer container) throws DeployException {
		try {
			File file = new File(container.getDeploymentDir(),
					container.getFilename());
			FileUtils.writeByteArrayToFile(file, container.getData());
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

}

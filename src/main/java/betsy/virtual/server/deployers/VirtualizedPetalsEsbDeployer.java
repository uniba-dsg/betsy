package betsy.virtual.server.deployers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;

/**
 * Deployer for the virtualized PetalsESB engine.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public class VirtualizedPetalsEsbDeployer implements VirtualizedEngineDeployer {

	private static final Logger log = Logger
			.getLogger(VirtualizedPetalsEsbDeployer.class);

	@Override
	public String getName() {
		return "petalsesb_v";
	}

	private File getDeployDestination(DeployOperation container) {
		return new File(container.getDeploymentDir(), container
				.getFileMessage().getFilename());
	}

	private boolean isDeployedFileAvailable(DeployOperation container) {
		return getDeployDestination(container).isFile();
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

		long start = -System.currentTimeMillis();

		// Be careful: here the process is deployed if the file is missing!
		while (isDeployedFileAvailable(container)
				&& (System.currentTimeMillis() + start < container
						.getDeployTimeout())) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (isDeployedFileAvailable(container)) {
			// timed out :/
			throw new DeployException("Process could not be deployed within "
					+ container.getDeployTimeout() + "seconds. The operation "
					+ "timed out.");
		}

		log.debug("File deployment done");

		// process is deployed, now wait for verification in logfile
		File logfile = new File(container.getEngineLogfileDir(), "petals.log");
		String successMessage = "Service Assembly '"
				+ container.getBpelFileNameWithoutExtension()
				+ "Application' started";
		String errorMessage = "Service Assembly '"
				+ container.getBpelFileNameWithoutExtension()
				+ "Application' deployed with some SU deployment in failure";
		DeploymentLogVerificator dlv = new DeploymentLogVerificator(logfile,
				successMessage, errorMessage);

		if (logfile.isFile()) {
			dlv.waitForDeploymentCompletition(container.getDeployTimeout());
		} else {
			log.warn("petals.log not found, skip log verification");
		}
	}
}

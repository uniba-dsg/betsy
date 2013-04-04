package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedOdeDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "ode_v";
	}

	@Override
	public void deploy(DeployContainer container) throws DeployException {
		try {
			unzipContainer(container);
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

	private void unzipContainer(DeployContainer container) throws IOException {
		// String path = "";
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(
				container.getData()));
		ZipEntry entry = null;
		File deployFolder = new File(container.getDeploymentDir(), container
				.getFilename().replace(".zip", ""));

		try {
			boolean createdParent = deployFolder.mkdirs();
			log.debug("Created parent dirs? " + createdParent);
			if (createdParent) {
				setPosixPermissions(deployFolder.toPath());
			}

			// unzip the zip file stored in the deployContainer
			while ((entry = zipStream.getNextEntry()) != null) {
				// prepare
				String entryName = entry.getName();
				File outputFile = new File(deployFolder, entryName);

				// write content
				FileOutputStream out = new FileOutputStream(outputFile);
				byte[] buf = new byte[4096];
				int bytesRead = 0;
				while ((bytesRead = zipStream.read(buf)) != -1) {
					out.write(buf, 0, bytesRead);
				}
				out.close();
				zipStream.closeEntry();

				// make file at least readable for every user
				setPosixPermissions(outputFile.toPath());
			}
		} finally {
			zipStream.close();
		}
	}

	private void setPosixPermissions(final Path path) {
		try {
			// using PosixFilePermission to set file permissions 777
			Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
			// set owner
			permissions.add(PosixFilePermission.OWNER_READ);
			permissions.add(PosixFilePermission.OWNER_WRITE);
			permissions.add(PosixFilePermission.OWNER_EXECUTE);
			// set group
			permissions.add(PosixFilePermission.GROUP_READ);
			permissions.add(PosixFilePermission.GROUP_WRITE);
			permissions.add(PosixFilePermission.GROUP_EXECUTE);
			// set other
			permissions.add(PosixFilePermission.OTHERS_READ);
			permissions.add(PosixFilePermission.OTHERS_WRITE);
			permissions.add(PosixFilePermission.OTHERS_EXECUTE);

			Files.setPosixFilePermissions(path, permissions);
			log.debug("New file permissions successfully applied to '"
					+ path.toString() + "'.");
		} catch (IOException exception) {
			log.warn("Exception while setting new file permissions:", exception);
		}
	}

	@Override
	public void onPostDeployment(DeployContainer container)
			throws DeployException {
		log.info("waiting for the ode_v deployment process to fire");

		boolean deployedFileAvailable = isDeployedFileAvailable(container);
		long start = -System.currentTimeMillis();
		int deployTimeout = container.getDeployTimeout();

		while (!deployedFileAvailable
				&& (System.currentTimeMillis() + start < deployTimeout)) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore
			}
			deployedFileAvailable = isDeployedFileAvailable(container);
		}

		if (!deployedFileAvailable) {
			// timed out :/
			String msg = "Process could not be deployed within "
					+ deployTimeout + "seconds. .deployed not found. The "
					+ "operation timed out.";
			log.warn(msg);
			throw new DeployException(msg);
		}

		// process is deployed, now wait for verification in logfile
		boolean logVerification = false;
		File catalinaLog = new File(container.getEngineLogfileDir(),
				"catalina.out");
		String successMessage = "Deployment of artifact "
				+ container.getBpelFileNameWithoutExtension() + " successful";
		String errorMessage = "Deployment of "
				+ container.getBpelFileNameWithoutExtension() + " failed";
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
			if (!logVerification) {
				String msg = "Process could not be deployed within "
						+ deployTimeout + "seconds. Log verification failed. "
						+ "The operation timed out.";
				log.warn(msg);
				throw new DeployException(msg);
			}
		} else {
			log.warn("Catalina.out not found, wait 2s for deployment");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	private boolean isDeployedFileAvailable(DeployContainer container) {
		File file = new File(container.getDeploymentDir() + "/"
				+ container.getBpelFileNameWithoutExtension() + ".deployed");
		return file.isFile();
	}
}

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

public class VirtualizedOpenEsbDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "openesb_v";
	}

	@Override
	public void deploy(DeployOperation operation) throws DeployException {
		// write data to temp dir first
		File tmpDeployFile = new File("tmp", operation.getFileMessage().getFilename());
		createLocalDeployFile(tmpDeployFile, operation);
		log.info("Temporary deploy file written to disk");

		File deployTool = new File(operation.getDeploymentExecutable());
		if (!deployTool.exists()) {
			throw new DeployException("Deployment failed: Deployment tools "
					+ "of OpenESB have not been found at '"
					+ deployTool.getAbsolutePath() + "'");
		}

		Runtime runtime = Runtime.getRuntime();

		String[] deployAtt = { operation.getDeploymentExecutable(),
				"deploy-jbi-service-assembly",
				tmpDeployFile.getAbsolutePath() };
		BufferedReader buffDeploy = null;
		try {
			Process proc = runtime.exec(deployAtt);
			InputStream inStr = proc.getInputStream();
			buffDeploy = new BufferedReader(new InputStreamReader(inStr));
			String str;
			log.debug("Deploy console output:");
			while ((str = buffDeploy.readLine()) != null) {
				log.debug("--:" + str);
			}
		} catch (IOException exception) {
			if (buffDeploy != null) {
				try {
					buffDeploy.close();
				} catch (IOException e) {
					// ignore
				}
			}
			throw new DeployException("Deployment failed because of an "
					+ "unexpected exception:", exception);
		}

		String[] StartAtt = { operation.getDeploymentExecutable(),
				"start-jbi-service-assembly",
				operation.getBpelFileNameWithoutExtension() + "Application" };
		BufferedReader buffStart = null;
		try {
			Process proc = runtime.exec(StartAtt);
			InputStream inStr = proc.getInputStream();
			buffStart = new BufferedReader(new InputStreamReader(inStr));
			String str;
			log.debug("Start console output:");
			while ((str = buffStart.readLine()) != null) {
				log.debug("--:" + str);
			}
		} catch (IOException exception) {
			if (buffStart != null) {
				try {
					buffStart.close();
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
		// not needed as using synchronous deployment
	}

}

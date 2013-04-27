package betsy.virtual.server.deployers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.messages.DeployOperation;


public class VirtualizedOracleDeployer implements VirtualizedEngineDeployer {

	private static final Logger log = Logger.getLogger(VirtualizedOracleDeployer.class);

	@Override
	public String getName() {
		return "oracle_v";
	}

	@Override
	public void deploy(DeployOperation operation) throws DeployException {
		// write data to temp dir first
		File tmpDeployFile = new File("tmp", operation.getFileMessage().getFilename());
		createLocalDeployFile(tmpDeployFile, operation);
		log.info("Temporary deploy file written to disk");

		File deployTool = new File(operation.getDeploymentFile());
		if (!deployTool.exists()) {
			throw new DeployException("Deployment failed: Deployment tools "
					+ "of Oracle have not been found at '"
					+ deployTool.getAbsolutePath() + "'");
		}
		
		Runtime runtime = Runtime.getRuntime();

		String[] deployAtt = { "ant", "-f",
				operation.getDeploymentFile(),
				"-DserverURL=http://localhost:7001", 
				"-DsarLocation="+tmpDeployFile.getAbsolutePath(),
				"-Duser=weblogic",
				"-Dpassword=welcome1",
				"-Doverwrite=true",
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
	public void onPostDeployment(DeployOperation operation)
			throws DeployException {
		// not needed as using synchronous deployment
	}

}

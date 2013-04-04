package de.uniba.wiai.dsg.betsy.virtual.server.deployers;

import groovy.util.AntBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployContainer;
import de.uniba.wiai.dsg.betsy.virtual.server.comm.VirtualizedEngineDeployer;

public class VirtualizedOrchestraDeployer implements VirtualizedEngineDeployer {

	private Logger log = Logger.getLogger(getClass());

	@Override
	public String getName() {
		return "orchestra_v";
	}

	@Override
	public void deploy(DeployContainer container) throws DeployException {

		// TODO write data to temp dir
		File tmpFile = new File("tmp/" + container.getFilename());
		createLocalDeployFile(tmpFile, container);
		log.info("Temporary deploy file written to disk");
		
		// TODO requires Ant on system
		AntBuilder ant = new AntBuilder();

		Map<String, Object> map = new HashMap<>();
		map.put("executable", "ant");
		// TODO check where directory is located
		map.put("dir", "${engine.serverPath}/orchestra-cxf-tomcat-4.9.0");
		map.put("dir", container.getDeploymentDir());
		// TODO verify if is working
		map.put("arg", "deploy");
		map.put("arg", "-Dbar=" + tmpFile.getAbsolutePath().toString());

		map.put("osfamily", "unix");
		ant.invokeMethod("exec", map);

	}

	private void createLocalDeployFile(File tmpFile, DeployContainer container) throws DeployException {
		try {
			FileUtils.writeByteArrayToFile(tmpFile, container.getData());
		} catch (IOException exception) {
			throw new DeployException("Couldn't write the container data to "
					+ "the local disk:", exception);
		}
	}

	@Override
	public void onPostDeployment(DeployContainer container)
			throws DeployException {
		// do nothing - as using synchronous deployment
	}

}

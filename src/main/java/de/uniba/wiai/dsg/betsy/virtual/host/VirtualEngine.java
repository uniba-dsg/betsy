package de.uniba.wiai.dsg.betsy.virtual.host;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import betsy.data.Process;
import betsy.data.engines.Engine;
import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.common.Checksum;
import de.uniba.wiai.dsg.betsy.virtual.common.comm.CommClient;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ChecksumException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.CollectLogfileException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.ConnectionException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.DeployException;
import de.uniba.wiai.dsg.betsy.virtual.common.exceptions.InvalidResponseException;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DataContainer;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.DeployOperation;
import de.uniba.wiai.dsg.betsy.virtual.common.messages.LogfileCollection;
import de.uniba.wiai.dsg.betsy.virtual.host.comm.TCPCommClient;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.DownloadException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortRedirectException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.PortUsageException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.TestFailedException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.archive.ArchiveException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.engine.VirtualizedEngineServiceException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualMachineException;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.PortVerifier;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.VirtualMachineImporter;

//TODO Javadoc
public abstract class VirtualEngine extends Engine implements
		VirtualizedEngineAPI {

	public static final String VIRTUAL_NAME_PREFIX = "betsy-";
	private static final Logger log = Logger.getLogger(VirtualEngine.class);

	private final Configuration config = Configuration.getInstance();
	private final VirtualBoxController vbController;
	private final CommClient comm;

	private VirtualMachine vm = null;

	public VirtualEngine(VirtualBoxController vbc) {
		if (vbc == null) {
			throw new IllegalArgumentException("vbc must not be null!");
		}
		this.vbController = vbc;
		this.setPackageBuilder(new VirtualEnginePackageBuilder(getName()));
		// create communication handler
		comm = new TCPCommClient("127.0.0.1", 48888);
	}

	private static File getDownloadPath() {
		return new File("vm_download");
	}

	private static File getExtractPath() {
		return new File("vm_extraction");
	}

	public static void cleanAll() throws IOException {
		log.info("Removing all virtualized engine content");

		// File downloadFolder = getDownloadPath();
		FileUtils.forceDelete(getDownloadPath());

		// File extractionFolder = getExtractPath();
		FileUtils.forceDelete(getExtractPath());

		// TODO delete all imported machines of betsy
	}

	@Override
	public String getVirtualMachineName() {
		return VIRTUAL_NAME_PREFIX + this.getName();
	}

	@Override
	public boolean isVirtualMachineReady() {
		if (isVMImported()) {
			VirtualMachine vm;
			try {
				vm = vbController.getVirtualMachine(getVirtualMachineName());
				return vm.containsRunningSnapshot();
			} catch (VirtualMachineException e) {
				// should not happen as vm was already found
			}
		}
		return false;
	}

	@Override
	public boolean isVMImported() {
		return vbController.containsMachine(this.getVirtualMachineName());
	}

	@Override
	public void startup() {
		log.debug("Startup virtualized engine " + getName() + " ...");

		try {
			// verify port usage
			PortVerifier verifier = new PortVerifier();
			Set<Integer> ports = getRequiredPorts();
			// also verify the bVMS port
			ports.add(48888);
			verifier.verify(ports);

			// forward and verify used ports
			this.vm.applyPortForwarding(getRequiredPorts());

			// start headless?
			boolean headless = config.getValueAsBoolean(
					"virtualisation.engines." + getName() + ".headless", false);
			this.vm.start(headless);
			log.debug("...VM started");
		} catch (PortUsageException exception) {
			log.error("The VM could not be started properly:", exception);
			throw new TestFailedException("The VM could not be started "
					+ "properly:", exception, false);
		} catch (PortRedirectException exception) {
			log.error("The VM could not be started properly:", exception);
			throw new TestFailedException("The VM could not be started "
					+ "properly:", exception, true);
		}

		// start communication client-part
		try {
			Integer timeout = config.getValueAsInteger(
					"virtualisation.bvms.requestTimeout", 120);
			comm.reconnect(timeout * 1000);
			log.trace("...connected to bVMS");
		} catch (IOException exception) {
			// stop VM before doing anything else
			shutdown();

			log.error("The VM was started, but the communication to betsy's "
					+ "server could not be established. Please verify the server "
					+ "is properly installed.");
			throw new TestFailedException(
					"The VM was started, but the communication to betsy's "
							+ "server could not be established. Please verify "
							+ "the server is properly installed.", exception,
					true);
		}

		try {
			comm.sendEngineInformation(getName());
			log.trace("...engine info sent to bVMS");
		} catch (ConnectionException exception) {
			throw new TestFailedException("The VM was started, but the "
					+ "engine information could not be send properly.",
					exception, true);
		} catch (InvalidResponseException exception) {
			throw new TestFailedException("The VM was started, but the "
					+ "engine information could not be send properly.",
					exception, false);
		}

		log.trace("...startup done!");
	}

	@Override
	public void shutdown() {
		log.debug("Shutdown virtualized engine " + getName() + " ...");
		// stop communication
		comm.disconnect();
		// if there is no virtualMachine then there is nothing to stop
		if (this.vm != null) {
			boolean saveState = config.getValueAsBoolean(
					"virtualisation.engines." + getName()
							+ ".shutdownSaveState", false);
			if (saveState) {
				this.vm.saveState();
			} else {
				this.vm.stop();
			}
		}
		log.trace("...shutdown done!");
	}

	@Override
	public void install() {
		log.debug("Install virtualized engine " + getName() + " ...");
		try {
			if (!isVirtualMachineReady()) {
				if (!isVMImported()) {
					log.debug(String.format("VM '%s' is not imported yet.",
							getVirtualMachineName()));
					VirtualMachineImporter importer = new VirtualMachineImporter(
							getVirtualMachineName(), getName(),
							getDownloadPath(), getExtractPath(),
							this.vbController);

					importer.importVirtualMachine();
				}
				this.vm = vbController
						.getVirtualMachine(getVirtualMachineName());
				if (!this.vm.containsRunningSnapshot()) {
					// need to create a running snapshot
					this.vm.createRunningSnapshot(getName(),
							getRequiredAddresses(), getRequiredPorts());
				}
			}

			this.vm = vbController.getVirtualMachine(getVirtualMachineName());

			// reset to snapshot
			this.vm.resetToLatestSnapshot();
		} catch (VirtualMachineNotFoundException
				| VirtualizedEngineServiceException | PortUsageException
				| ArchiveException | DownloadException exception) {
			// DownloadException: was not imported yet, nothing to delete
			// ArchiveException: was not imported yet, nothing to delete
			// PortException: was imported, can be kept in virtualBox
			// VBServiceException: import can be kept, do NOT delete the VM
			log.error("The VMs installation could not be processed", exception);
			throw new TestFailedException("The VMs installation "
					+ "could not be processed:", exception, false);
		} catch (PortRedirectException exception) {
			log.error("The VMs installation could not be processed", exception);
			throw new TestFailedException("The VMs installation "
					+ "could not be processed:", exception, true);
		} catch (InterruptedException exception) {
			log.error("The VMs installation could not be processed. The "
					+ "snapshot creation was interrupted:", exception);
			throw new TestFailedException("The VMs installation "
					+ "could not be processed. The "
					+ "snapshot creation was interrupted:", exception, true);
		}
		log.trace("...installation done!");
	}

	@Override
	public DeployOperation buildDeployContainer(Process process)
			throws IOException {
		Path path = Paths.get(process
				.getTargetPackageFilePath(getTargetPackageExtension()));
		Path filenamePath = path.getFileName();
		String filename = filenamePath.toString();
		byte[] data = Files.readAllBytes(path);
		Checksum checksum = new Checksum(data);
		DeployOperation container = new DeployOperation(filename,
				process.getBpelFileNameWithoutExtension(), data, getName(),
				getVMDeploymentTimeout(), getVMDeploymentDir(), getVMLogfileDir(),
				checksum);
		return container;
	}

	@Override
	public void deploy(Process process) {
		try {
			log.debug("Deploy virtualized engine " + getName() + ", process: "
					+ process.toString() + " ...");

			// resend once in case of checksum exception
			int attemptsLeft = 2;
			while (attemptsLeft > 0) {
				attemptsLeft--;
				try {
					executeDeploy(process);
					log.info("...deploy done!");
					return;
				} catch (ChecksumException exception) {
					if (attemptsLeft <= 0) {
						throw new TestFailedException(exception, true);
					}
				}
			}
		} catch (TestFailedException exception) {
			try {
				process.getEngine().storeLogs(process);
			} catch (TestFailedException exception2) {
				log.info("Could not store logfiles for failed deployment:",
						exception2);
			}
			throw exception;
		}
	}

	private void executeDeploy(Process process) throws ChecksumException {
		if (comm.isConnected()) {
			try {
				DeployOperation container = buildDeployContainer(process);
				comm.sendDeploy(container);
			} catch (IOException | ConnectionException | DeployException exception) {
				throw new TestFailedException(exception, true);
			} catch (InvalidResponseException exception) {
				throw new TestFailedException(exception, false);
			}
		} else {
			throw new TestFailedException("VirtualEngine can only be "
					+ "deployed if the connection to the server is alive.",
					true);
		}
	}

	@Override
	public abstract Integer getEndpointPort();

	@Override
	public abstract String getEndpointPath(Process process);

	private String getEndpointHost() {
		return "http://localhost:" + getEndpointPort();
	}

	@Override
	public String getEndpointUrl(Process process) {
		String host = getEndpointHost();
		String path = getEndpointPath(process);
		host = host.endsWith("/") ? host.substring(0, host.length() - 1) : host;
		path = path.startsWith("/") ? path : "/" + path;
		return host + path;
	}

	@Override
	public void storeLogs(Process process) {
		log.debug("Store logs for engine " + getName() + " ...");
		try {
			LogfileCollection lfc = null;

			// request once again in case of checksum exception
			for (int attempt = 1; attempt < 3; attempt++) {
				try {
					// show where to get them from
					String bVMSDir = config.getValueAsString(
							"virtualisation.bvms.installationDir",
							"/opt/betsy/");
					bVMSDir = bVMSDir.endsWith("/") ? bVMSDir : bVMSDir + "/";
					bVMSDir += "log";

					// collect logs from remote server
					lfc = comm.getLogfilesFromServer(bVMSDir,
							this.getVMLogfileDir());
					break;
				} catch (ChecksumException exception) {
					if (attempt >= 2) {
						throw new TestFailedException("Test failed while "
								+ "collecting the logfiles. There were "
								+ "repeated checksum exceptions:", exception,
								true);
					}
				}
			}

			try {
				if (lfc != null) {
					// create log folders
					File processLogFolder = new File(process.getTargetPath()
							+ "/logs");
					processLogFolder.mkdirs();
					File engineLogFolder = new File(processLogFolder, "engine");
					engineLogFolder.mkdir();
					File betsyLogFolder = new File(processLogFolder,
							"betsy-server");
					betsyLogFolder.mkdir();

					// save to disk...
					for (DataContainer lf : lfc.getEngineLogfiles()) {
						File f = new File(engineLogFolder, lf.getFilename());
						FileUtils.writeByteArrayToFile(f, lf.getData());
					}
					for (DataContainer lf : lfc.getBetsyLogfiles()) {
						File f = new File(betsyLogFolder, lf.getFilename());
						FileUtils.writeByteArrayToFile(f, lf.getData());
					}
				}
			} catch (IOException exception) {
				throw new TestFailedException("Test failed as we could not "
						+ "write the resulting logfiles to the disk.",
						exception, true);
			}
		} catch (CollectLogfileException | ConnectionException exception) {
			// usually temporary failures --> test can be repeated
			throw new TestFailedException("Test failed while collecing the "
					+ "logfiles:", exception, true);
		} catch (InvalidResponseException exception) {
			// permanent failure, maybe incompatible versions
			throw new TestFailedException("Test failed while collecing the "
					+ "logfiles:", exception, false);
		}

		log.trace("...storing logs done!");
	}

	@Override
	public void failIfRunning() {
		// raise exception if VM is imported AND is already running
		VirtualMachine tmpVm = this.vm;
		if (this.vm == null) {
			try {
				tmpVm = vbController.getVirtualMachine(getVirtualMachineName());
			} catch (VirtualMachineNotFoundException e) {
				// ignore, can't be running if not found
				return;
			}
		}

		if (tmpVm.isActive()) {
			throw new TestFailedException("VirtualMachine is already running.");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (getClass() != o.getClass()) {
			return false;
		}

		VirtualEngine engine = (VirtualEngine) o;

		if (getName() != engine.getName()) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public Integer getVMDeploymentTimeout() {
		Integer timeout = config.getValueAsInteger("virtualisation.engines."
				+ getName() + ".deploymentTimeout", 20);
		return timeout * 1000;
	}

}

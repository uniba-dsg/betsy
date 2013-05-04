package betsy.virtual.host;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import betsy.Configuration;
import betsy.data.Process;
import betsy.data.engines.Engine;
import betsy.virtual.common.exceptions.ChecksumException;
import betsy.virtual.common.exceptions.CollectLogfileException;
import betsy.virtual.common.exceptions.ConnectionException;
import betsy.virtual.common.exceptions.DeployException;
import betsy.virtual.common.exceptions.InvalidResponseException;
import betsy.virtual.common.messages.DeployOperation;
import betsy.virtual.common.messages.FileMessage;
import betsy.virtual.common.messages.LogfileCollection;
import betsy.virtual.host.comm.CommClient;
import betsy.virtual.host.comm.TCPCommClient;
import betsy.virtual.host.exceptions.DownloadException;
import betsy.virtual.host.exceptions.PermanentFailedTestException;
import betsy.virtual.host.exceptions.PortUsageException;
import betsy.virtual.host.exceptions.TemporaryFailedTestException;
import betsy.virtual.host.exceptions.VirtualizedEngineServiceException;
import betsy.virtual.host.exceptions.archive.ArchiveException;
import betsy.virtual.host.exceptions.vm.PortRedirectException;
import betsy.virtual.host.exceptions.vm.VirtualMachineNotFoundException;
import betsy.virtual.host.utils.PortVerifier;
import betsy.virtual.host.utils.VirtualMachineImporter;

/**
 * A {@link VirtualizedEngine} does not install and use an engine server on the
 * current host, but does use an engine installed in a virtualized environment.
 * It therefore offers the same functionality as the default {@link Engine} but
 * also has some more functions to handle the virtualized surrounding.
 * 
 * @author Cedric Roeck
 * @version 1.0
 */
public abstract class VirtualizedEngine extends Engine implements
		VirtualizedEngineAPI {

	public static final String VIRTUAL_NAME_PREFIX = "betsy-";

	private static final Logger log = Logger.getLogger(VirtualizedEngine.class);

	private final Configuration config = Configuration.getInstance();
	private final VBoxController vbController;
	private final CommClient comm;

	private boolean initialized = false;
	private VirtualMachine vm = null;

	public VirtualizedEngine() {
		this.vbController = new VBoxController();
		// create communication handler
		comm = new TCPCommClient("127.0.0.1", 48888);
	}

	private static File getDownloadPath() {
		return new File("vm_download");
	}

	private static File getExtractPath() {
		return new File("vm_extraction");
	}

	@Override
	public void prepare() {
		// ensure folder structure
		super.prepare();
		initialize();
	}

	private void initialize() {
		if (!initialized) {
			log.trace("Initializing VirtualizedEngine");
			// load VBox specific stuff
			vbController.init();
			initialized = true;
		}
	}

	@Override
	public String getVirtualMachineName() {
		return VIRTUAL_NAME_PREFIX + this.getName();
	}

	/**
	 * Check if the VirtualMachine is imported and has at least one snapshot
	 * that can be used to reset to.
	 * 
	 * @return true if VM is imported and has at least one Snapshot
	 */
	private boolean isVirtualMachineReady() {
		if (isVMImported()) {
			VirtualMachine vm;
			try {
				vm = vbController.getVirtualMachine(getVirtualMachineName());
				return vm.hasRunningSnapshot();
			} catch (VirtualMachineNotFoundException e) {
				// should not happen as vm was already found
			}
		}
		return false;
	}

	/**
	 * Check if the Engine's VM is already imported.
	 * 
	 * @return true if VM is imported
	 */
	private boolean isVMImported() {
		return vbController.containsMachine(this.getVirtualMachineName());
	}

	@Override
	public void startup() {
		log.debug("Startup virtualized engine " + getName() + " ...");
		// required for compatibility with EngineControl
		initialize();
		try {
			// verify port usage
			Set<Integer> ports = getRequiredPorts();
			// also verify the bVMS port
			ports.add(48888);
			PortVerifier.verify(ports);
			// forward and verify used ports
			this.vm.applyPortForwarding(ports);
			// start headless?
			boolean headless = config.getValueAsBoolean(
					"virtualisation.engines." + getName() + ".headless", false);
			this.vm.start(headless);
			log.debug("...VM started");
			// start communication client-part
			Integer timeout = config.getValueAsInteger(
					"virtualisation.bvms.requestTimeout", 120);
			comm.reconnect(timeout * 1000);
			log.trace("...connected to bVMS");
			comm.sendEngineInformation(getName());
			log.trace("...engine info sent to bVMS");
		} catch (ConnectionException exception) {
			throw new TemporaryFailedTestException("The VM was started, but "
					+ "the engine information could not be send properly.",
					exception);
		} catch (InvalidResponseException exception) {
			throw new PermanentFailedTestException("The VM was started, but "
					+ "the engine information could not be send properly.",
					exception);
		} catch (IOException exception) {
			throw new TemporaryFailedTestException("The VM was started, but "
					+ "the communication to betsy's server could not be "
					+ "established. Please verify the server is properly "
					+ "installed.", exception);
		} catch (PortUsageException exception) {
			throw new PermanentFailedTestException("The VM could not be "
					+ "started properly:", exception);
		} catch (PortRedirectException exception) {
			throw new TemporaryFailedTestException("The VM could not be "
					+ "started properly:", exception);
		}
		log.trace("...startup done!");
	}

	@Override
	public void shutdown() {
		// required for compatibility with EngineControl
		initialize();

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
		// required for compatibility with EngineControl
		initialize();
		try {
			if (!isVirtualMachineReady()) {
				if (!isVMImported()) {
					log.debug(String.format("VM '%s' is not imported yet.",
							getVirtualMachineName()));
					VirtualMachineImporter importer = new VirtualMachineImporter(
							getVirtualMachineName(), getName(),
							getDownloadPath(), getExtractPath(),
							this.vbController);

					importer.executeVirtualMachineImport();
				}
				this.vm = vbController
						.getVirtualMachine(getVirtualMachineName());
				if (!this.vm.hasRunningSnapshot()) {
					boolean headless = config
							.getValueAsBoolean("virtualisation.engines."
									+ getName() + ".headless", false);
					// need to create a running snapshot
					this.vm.createRunningSnapshot(getName(),
							getVerifiableServiceAddresses(),
							getRequiredPorts(), headless);
				}
			}

			this.vm = vbController.getVirtualMachine(getVirtualMachineName());

			// reset to snapshot
			this.vm.resetToLatestSnapshot();
		} catch (VirtualMachineNotFoundException
				| VirtualizedEngineServiceException | ArchiveException
				| PortUsageException | DownloadException exception) {
			throw new PermanentFailedTestException("The VMs installation "
					+ "could not be processed:", exception);
		} catch (PortRedirectException exception) {
			throw new TemporaryFailedTestException("The VMs installation "
					+ "could not be processed:", exception);
		} catch (InterruptedException exception) {
			throw new TemporaryFailedTestException("The VMs installation "
					+ "could not be processed. The snapshot creation was "
					+ "interrupted:", exception);
		}
		log.trace("...installation done!");
	}

	@Override
	public DeployOperation buildDeployOperation(Process process)
			throws IOException {
		// basic deploy operation, sufficient for most engines
		Path path = getDeployableFilePath(process);
		Path filenamePath = path.getFileName();
		String filename = filenamePath.toString();
		byte[] data = Files.readAllBytes(path);

		DeployOperation operation = new DeployOperation();
		FileMessage fm = new FileMessage(filename, data);
		operation.setFileMessage(fm);
		operation.setEngineName(getName());
		operation.setBpelFileNameWithoutExtension(process
				.getBpelFileNameWithoutExtension());
		operation.setEngineLogDir(getVMLogfileDir());
		operation.setDeploymentDir(getVMDeploymentDir());
		operation.setDeployTimeout(getVMDeploymentTimeout());

		return operation;
	}

	@Override
	public void deploy(Process process) {
		try {
			log.debug("Deploying virtualized engine " + getName()
					+ ", process: " + process.toString() + " ...");

			// resend once in case of checksum exception
			int attemptsLeft = 2;
			while (attemptsLeft > 0) {
				attemptsLeft--;
				try {
					if (comm.isConnected()) {
						try {
							DeployOperation container = buildDeployOperation(process);
							comm.sendDeploy(container);
						} catch (IOException | ConnectionException
								| DeployException exception) {
							throw new TemporaryFailedTestException(exception);
						} catch (InvalidResponseException exception) {
							throw new PermanentFailedTestException(exception);
						}
					} else {
						throw new TemporaryFailedTestException("VirtualEngine "
								+ "can only be deployed if the connection "
								+ "to the server is alive.");
					}
					log.trace("...deploy done!");
					return;
				} catch (ChecksumException exception) {
					if (attemptsLeft <= 0) {
						throw new TemporaryFailedTestException(exception);
					}
				}
			}
		} catch (TemporaryFailedTestException exception) {
			try {
				process.getEngine().storeLogs(process);
			} catch (TemporaryFailedTestException exception2) {
				log.info("Could not store logfiles of the failed deployment:",
						exception2);
			}
			throw exception;
		}
	}

	@Override
	public void storeLogs(Process process) {
		log.debug("Storing logs for engine " + getName() + " ...");
		try {
			LogfileCollection lfc = null;

			// request once again in case of checksum exception
			for (int attempt = 1; attempt < 3; attempt++) {
				try {
					// collect logs from remote server
					lfc = comm.getLogfilesFromServer(this.getVMbVMSDir(),
							this.getVMLogfileDir());
					break;
				} catch (ChecksumException exception) {
					if (attempt >= 2) {
						throw new TemporaryFailedTestException("Test failed "
								+ "while collecting the logfiles. There were "
								+ "repeated checksum exceptions:", exception);
					}
				}
			}
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
				for (FileMessage lf : lfc.getEngineLogfiles()) {
					File f = new File(engineLogFolder, lf.getFilename());
					FileUtils.writeByteArrayToFile(f, lf.getData());
				}
				for (FileMessage lf : lfc.getBetsyLogfiles()) {
					File f = new File(betsyLogFolder, lf.getFilename());
					FileUtils.writeByteArrayToFile(f, lf.getData());
				}
			}
		} catch (IOException exception) {
			throw new TemporaryFailedTestException("Test failed as we "
					+ "could not write the resulting logfiles to "
					+ "the disk.", exception);
		} catch (CollectLogfileException | ConnectionException exception) {
			// usually temporary failures --> test can be repeated
			throw new TemporaryFailedTestException("Test failed while "
					+ "collecting the logfiles:", exception);
		} catch (InvalidResponseException exception) {
			// permanent failure, maybe incompatible versions
			throw new PermanentFailedTestException("Test failed while "
					+ "collecting the logfiles:", exception);
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
			throw new PermanentFailedTestException("VirtualMachine is "
					+ "already running.");
		}
	}

	@Override
	public Integer getVMDeploymentTimeout() {
		Integer timeout = config.getValueAsInteger("virtualisation.engines."
				+ getName() + ".deploymentTimeout", 30);
		return timeout * 1000;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (getClass() != o.getClass()) {
			return false;
		}

		VirtualizedEngine engine = (VirtualizedEngine) o;

		if (getName() != engine.getName()) {
			return false;
		}
		if (getVirtualMachineName() != engine.getVirtualMachineName()) {
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
		result = prime
				* result
				+ (getVirtualMachineName() != null ? getVirtualMachineName()
						.hashCode() : 0);
		return result;
	}

}

package betsy.bpel.tools

import betsy.bpel.cli.EngineParser
import betsy.bpel.engines.AbstractBPELEngine
import betsy.bpel.virtual.host.engines.AbstractVirtualBPELEngine

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * The {@link VirtualMachineInstaller} simplifies the task to create new virtual box machines for the virtual engines that are supported by betsy.<br>
 * In order to install a VM for a virtual engine you need to provide the basic image (either the UUID or the unique name) and specify some more options, 
 * for instance if you would like to overwrite existing VMs in case of name conflicts. The machines do not automatically create a snapshot as this is also done by the betsy tests itself.<br>
 * <br>
 * Usually the two projects, 'betsy' and 'betsy-engines' are in directories side-by-side. Otherwise you have to specify the location of both projects!
 * <br>
 * <b>Most common pitfalls:</b><br>
 * <ul>
 * <li>gradlew nicht ausführbar (unix only)</li>
 * <li>sprinkle nicht installiert</li>
 * <li>sprinkle Fehler</li>
 * <li>VirtualBox nicht installiert</li>
 * <li>VirtualBox Fehler</li>
 * </ul>
 */
class VirtualMachineInstaller {

	static def error(def msg) {
		println "ERROR >> ${msg}"
	}

	private VirtualBoxManage vbox
	private Sprinkle sprinkle
	private def options
	private def engines

	VirtualMachineInstaller(def options, def engines) {
		this.options = Objects.requireNonNull(options)
		this.engines = Objects.requireNonNull(engines)
	}

	public static void main(String[] args) {
		// Fetch the user options
		CliBuilder cli = new CliBuilder(usage: "[options] <engines>")
		cli.f(longOpt:'force',"force creating new VMs, will delete existing VMs with the same name")
		cli.k(longOpt:'keep',"keep VMs for failed installation attempts")
		cli.v(longOpt:'verbose',"show all output of the executed commands, not only the current steps")
		cli.u(args:1, argName:'uuid', longOpt:'uuid', "uuid of the basic virtual machine, which will be cloned to install the engines. Can also be the unique name of the virtual machine.")
		cli.b(args:1, argName:'directory', longOpt:'betsy', "path where betsy is located, defaults to current dir")
		cli.e(args:1, argName:'directory', longOpt:'engines', "path where the sprinkle scripts for the virtual engines are located, defaults to '../betsy-engines'")
		cli.o(args:1, argName:'directory', longOpt:'output', "path where the created virtual machines should be exported to as .ova file. If not set, the VMs are not exported.")

		// parsing cli params
		def options = cli.parse(args)
		if (options == null || options == false) {
			throw new IllegalArgumentException("Input arguments ${args} were not parseable.")
		}

		// usage information if required
		if (options.h) {
			println cli.usage()
			System.exit(0)
		}

		// parsing processes and engines
		List<AbstractBPELEngine> engines = null
		try {
			engines = new EngineParser(options.arguments() as String[]).parse()
		} catch (IllegalArgumentException e) {
			println "----------------------"
			println "ERROR - ${e.message} - Did you misspell the name?"
			System.exit(0)
		}
		println "Engines: ${engines.collect{it.name}}"

		if (!engines.any {it instanceof AbstractVirtualBPELEngine}) {
			error "No virtual engine, abort installation"
			return
		}

		def installer = new VirtualMachineInstaller(options, engines)
		installer.install()
	}

	def install() {
		def betsyHome = options.b ?:""
		def betsyEnginesHome = options.e ?:"../betsy-engines"
		// basic virtual machine with name "betsy_basic_cloneable" or by given name/uuid
		def basicUuid = options.u ?: "betsy_basic_cloneable"

		vbox = new VirtualBoxManage(options.v)
		// verify all mandatory config options related to the VirtualBox installation
		if(!vbox.isValid()) {
			error "VirtualBox installation could not be verified, aborting installation"
			return false
		}
		println "VirtualBox installation found and verified"

		sprinkle = new Sprinkle(betsyEnginesHome, options.v)
		if(!sprinkle.isInstalled()) {
			error "Sprinkle installation could not be verified, aborting installation"
			return false
		}
		println "Sprinkle installation found and verified"

		def existingForwardingRules = vbox.getForwardingRuleNames(basicUuid)

		// make betsy-vms available for sprinkle and copy the file
		Files.copy(Paths.get("./build/libs/betsy-vms.jar"), Paths.get("${betsyEnginesHome}/files/betsy-vms/betsy-vms.jar"), [
			StandardCopyOption.REPLACE_EXISTING,
			StandardCopyOption.COPY_ATTRIBUTES] as StandardCopyOption[])

		// loop for all selected engines
		engines.each { engine ->
			if(installEngine(engine, basicUuid, existingForwardingRules)) {
				println "Engine '${engine.name}' successfully installed\n\n\n"
			}else {
				error "Engine '${engine.name}' not installed. Continue with next engine...\n\n\n"
			}
		}
	}

	private def installEngine(def engine, def basicUuid, def existingForwardingRuleNames) {
		def cloneName = "betsy-${engine.name}"
		def cloneUuid = UUID.randomUUID().toString()
		println "Installing VM for engine '${engine.name}' with name '${cloneName}':"

		if(!sprinkle.hasEngineRecipe(engine.name)) {
			println "No recipe found :("
			// no installer, skip to next engine
			return false
		}

		def vmExists = false
		try {
			vmExists = vbox.executeCommand("showvminfo ${cloneName} --machinereadable", 5_000)
		}catch(ignored) {
			// VM with this name does not exist
		}

		def undoInstallation = false
		try {
			if(vmExists) {
				// already existing VM with the same name found, which is assumed to be installed, therefore continue with next engine
				if(!options.f) {
					error "VM with the same name already exists, abort installation"
					// continue with next engine
					return false
				}
				// delete existing vm with the same name if forced
				println "VM with the name '${cloneName}' already exists, deleting existing VM..."
				vbox.deleteVM(cloneName)
			}
			
			vbox.cloneVM(basicUuid, cloneUuid, cloneName)

			vbox.addToBetsyGroup(cloneUuid)

			vbox.clearForwardingRules(cloneUuid, existingForwardingRuleNames)

			// sleep required, otherwise machine is not always ready
			Thread.sleep(2000)

			vbox.addForwardingRule(cloneUuid, "guestssh", 2222, 22)

			// sleep required, otherwise machine is not always ready
			Thread.sleep(2000)

			vbox.startVM(cloneUuid)

			if(!sprinkle.installEngine(engine.name)) {
				error "Sprinkle installation failed :/"
				// installation failed, delete the dirty VM after poweroff
				undoInstallation = true
				return false
			}

			if(options.o) {
				def ovaDirectory = Paths.get(options.o).toAbsolutePath()
				def ovaFile = ovaDirectory.resolve("betsy-${engine.name}.ova")
				println "dir: ${ovaDirectory}"
				// verify if directory exists
				if(Files.exists(ovaFile) && !options.f) {
					// handle export failed
					error "Export failed: The .ova file for the engine '${engine.name}' already exists. Please use the '-f' option to overwrite the file, change the directory or manually delte this file."
				}else {
					vbox.exportVM(cloneUuid, ovaFile)
				}
			}
			
			// installation successful
			return true
		}catch(e) {
			error "Ooops: "+e
			// if anything went wrong, shutdown and finally delete the VM
			undoInstallation = true
			return false
		}finally {
			try {
				vbox.stopVM(cloneUuid)
			}catch(ignored) {
				// ignore errors
			}

			// delete if we should not keep the VM explicitly
			if(undoInstallation && !options.k) {
				try {
					// sleep required, otherwise machine is not always ready
					Thread.sleep(2000)
					vbox.deleteVM(cloneUuid)
				}catch(ignored) {
					error "Deleting the dirty VM failed, please delete the VM with the name manually!"
					System.exit(1)
				}
			}
		}
	}

}

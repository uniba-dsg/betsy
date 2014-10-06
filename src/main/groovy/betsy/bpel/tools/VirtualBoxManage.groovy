package betsy.bpel.tools

import betsy.config.Configuration;

import java.nio.file.Files
import java.nio.file.Path

import org.apache.commons.lang.StringUtils

import betsy.bpel.virtual.host.VirtualBoxException

public class VirtualBoxManage {

	static def error(def msg) {
		println "ERROR >> ${msg}"
	}
	
	private Path vboxManage
	private boolean verbose
	
	VirtualBoxManage(def verbose) {
		this.config = Objects.requireNonNull(config)
		// make sure path is absolute!
		this.vboxManage = Configuration.getVBoxManage().toAbsolutePath()
		this.verbose = verbose ? true : false
	}
	
	boolean isValid() {
		config.verify()
		
		if(!Files.exists(vboxManage)) {
			error "VirtualBox manage file not found at '${vboxManage}'"
			return false
		}
		
		try {
			return executeCommand("-v", 5_000)
		}catch(e) {
			return false
		}
	}
	
	def executeCommand(String command, long timeoutInMs = 10_000) {
		String vbCommand = "${vboxManage} ${command}"
		println vbCommand
		
		def serr
		def sout
		def vboxProcess = vbCommand.execute()
		if(verbose) {
			vboxProcess.consumeProcessOutput(System.out, System.err)
		}else {
			sout = new StringBuffer()
			serr = new StringBuffer()
			vboxProcess.consumeProcessOutput(sout, serr)
		}
		vboxProcess.waitForOrKill(timeoutInMs)
		if (vboxProcess.exitValue()) {
			if (!verbose) {
				error serr
			}
			throw new VirtualBoxException("Execution of the command '${vbCommand}' failed")
		}
		return true
	}
	
	/* This method is needed to allow the deletion of vbox rules with spaces in the name!
	 * The use of the other method could also be replaced with this method
	 */
	def executeCommand(List<String> command, long timeoutInMs = 10_000) {
		def vbCommand = []
		vbCommand.add(vboxManage.toString())
		vbCommand.addAll(command*.toString())
		println vbCommand.join(" ")
		
		ProcessBuilder pb = new ProcessBuilder(vbCommand as List)
		def vboxProcess = pb.start()
		
		def serr
		def sout
		if(verbose) {
			vboxProcess.consumeProcessOutput(System.out, System.err)
		}else {
			sout = new StringBuffer()
			serr = new StringBuffer()
			vboxProcess.consumeProcessOutput(sout, serr)
		}
		vboxProcess.waitForOrKill(timeoutInMs)
		if (vboxProcess.exitValue()) {
			if (!verbose) {
				error serr
			}
			throw new VirtualBoxException("Execution of the command '${vbCommand}' failed")
		}
		return true
	}
	
	def executeRequest(String command, long timeoutInMs = 10_000) {
		def sout = new StringBuffer()
		def serr = new StringBuffer()
		
		String vbCommand = "${vboxManage} ${command}"
		println vbCommand
		
		def vboxProcess = vbCommand.execute()
		vboxProcess.consumeProcessOutput(sout, serr)
		vboxProcess.waitForOrKill(timeoutInMs)
		if (vboxProcess.exitValue()) {
			error serr
			throw new VirtualBoxException("Execution of the command '${vbCommand}' failed")
		}
		return sout
	}
	
	// TODO include in engine object itself
	def startVM(String uuid) {
		try {
			// move the cloned VM into the betsy group
			executeCommand(["startvm", uuid, "--type", "headless"], 90_000)
			println "VM '${uuid}' successfully started"
			return true
		}catch(e) {
			throw new VirtualBoxException("Starting the VM '${uuid}' failed", e)
		}
	}
	
	// TODO include in engine object itself
	def stopVM(String uuid) {
		try {
			// move the cloned VM into the betsy group
			executeCommand(["controlvm", uuid, "poweroff"], 90_000)
			println "VM '${uuid}' successfully stopped"
			return true
		}catch(e) {
			throw new VirtualBoxException("Stopping the VM '${uuid}' failed", e)
		}
	}
	
	// TODO include in engine object itself
	def deleteVM(String uuid) {
		// delete the VM again
		try {
			executeCommand(["unregistervm", uuid, "--delete"], 60_000)
			println "VM '${uuid}' successfully deleted"
			return true
		}catch(e) {
			throw new VirtualBoxException("Deleting the VM '${uuid}' failed", e)
		}
	}
	
	def cloneVM(String originUuid, String destinationUuid = UUID.randomUUID(), String destinationName) {
		try {
			executeCommand(["clonevm", originUuid, "--mode", "machine", "--name", destinationName, "--uuid", destinationUuid, "--register"], 120_000)
			println "VM '${destinationName}' successfully cloned from the origin"
			// sleep required, otherwise machine is not always ready
			Thread.sleep(2000)
			return true
		}catch(e) {
			throw new VirtualBoxException("Cloning failed", e)
		}
	}
	
	// TODO include in engine object itself
	def addToBetsyGroup(String uuid) {
		try {
			// move the cloned VM into the betsy group
			executeCommand(["modifyvm", uuid, "--groups", "/betsy-engines"], 10_000)
			println "VM '${uuid}' successfully moved into group 'betsy-engines'"
			return true
		}catch(e) {
			throw new VirtualBoxException("Moving into group 'betsy-engines' failed", e)
		}
	}
	
	// TODO include in engine object itself
	def getForwardingRuleNames(String uuid) {
		println "Searching for VM '${uuid}'..."
		def rules = []
		try {
			// get and process the information of the VM to be cloned
			def result = executeRequest("showvminfo ${uuid} --machinereadable", 5_000)
			
			result.eachLine { line ->
				if(line.startsWith("Forwarding(")) {
					// get name, which is the first value in a comma separated list
					rules.add(StringUtils.substringBetween(line, "\"", "\"").split(",")[0])
				}
			}
			return rules
		}catch(e) {
			// TODO shall we download and import the basic VM?
			throw new VirtualBoxException("Forwarding rules for the VM could not be found", e)
		}
	}
	
	def exportVM(String uuid, Path ovaFile) {
		try {
			// create directories if they don't exist yet
			if(!Files.isDirectory(ovaFile.parent)) {
				Files.createDirectories(ovaFile.parent)
			}
			// vbox requires us to delete the file first
			if(Files.exists(ovaFile)) {
				Files.delete(ovaFile)
			}
 			
			// can take up to 5 minutes
			executeCommand(["export", uuid, "--output", ovaFile], 600_000)
			println "VM successfully exported to ${ovaFile}"
			return true
		}catch(Exception e) {
			throw new VirtualBoxException("Exporting the VM failed", e)
		}
	}
	
	// TODO include in engine object itself
	def clearForwardingRules(String uuid, def ruleNames) {
		try {
			// delete all existing forwardingRules, which have been copied from the cloned VM
			ruleNames.each { rule ->
				executeCommand([/modifyvm/, uuid, /--natpf1/, /delete/, rule], 10_000)
				println "Port forwarding rule '${rule}' successfully deleted"
			}
			println "All Port forwarding rules (${ruleNames?.size()}) successfully deleted for '${uuid}'"
			return true
		}catch(Exception e) {
			throw new VirtualBoxException("Deleting port forwarding rules failed", e)
		}
	}
	
	// TODO include in engine object itself
	def addForwardingRule(String uuid, String ruleName, int srcPort, int destPort) {
		try {
			// establish port forwarding rule for the sprinkle installer, which is using the SSH port 22 on the VM
			executeCommand("modifyvm ${uuid} --natpf1 '${ruleName},tcp,,${srcPort},,${destPort}'", 10_000)
			println "VM '${uuid}' successfully established SSH port forwarding rule from port ${srcPort} to port ${destPort}"
			return true
		}catch(e) {
			throw new VirtualBoxException("Establishing SSH port forwarding failed", e)
		}
	}
	
}

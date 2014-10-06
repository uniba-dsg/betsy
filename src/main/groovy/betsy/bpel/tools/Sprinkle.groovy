package betsy.bpel.tools;

import org.apache.commons.lang.SystemUtils;

public class Sprinkle {

	static def error(def msg) {
		println "ERROR >> ${msg}"
	}

	def installerHomeDir
	boolean verbose

	Sprinkle(def installerHomeDir, def verbose) {
		this.installerHomeDir = Objects.requireNonNull(installerHomeDir)
		this.verbose = verbose ? true : false
	}

	def isInstalled() {
		// Test if sprinkle is available
		try {
			def sout
			def serr
			def process
			if(SystemUtils.IS_OS_WINDOWS) {
				process = "cmd /C sprinkle -v".execute()
			}else {
				process = "sprinkle -v".execute()
			}
			
			if(verbose) {
				process.consumeProcessOutput(System.out, System.err)
			}else {
				sout = new StringBuffer()
				serr = new StringBuffer()
				process.consumeProcessOutput(sout, serr)
			}
			
			process.waitForOrKill(10_000)
			if (process.exitValue()) {
				// unknown error, nevertheless sprinkle is most likely not usable
				if (!verbose) {
					error 'Sprinkle: >> ' + serr
				}
				return false
			}
		}catch(e) {
			error "The ruby gem 'Sprinkle' is currently not installed on your system"
			return false
		}
		return true
	}

	def hasEngineRecipe(def engineName) {
		// search for engine installer
		def installer = new File("${installerHomeDir}/${engineName}_server.rb")
		if(!installer.exists()) {
			error "Installer for engine '${engineName}' not found at location '${installer.absolutePath}'"
			return false
		}
		return true
	}

	def installEngine(def engineName) {
		if(!hasEngineRecipe(engineName)) {
			return false
		}

		// invoke sprinkle
		try {
			def sout
			def serr
			def command
			if(SystemUtils.IS_OS_WINDOWS) {
				command = "cmd /C sprinkle -v -s ${engineName}_server.rb"
			}else {
				command = "sprinkle -v -s ${engineName}_server.rb"
			}
			def process = command.execute(null, new File(installerHomeDir))
			
			if(verbose) {
				process.consumeProcessOutput(System.out, System.err)
			}else {
				sout = new StringBuffer()
				serr = new StringBuffer()
				process.consumeProcessOutput(sout, serr)
			}
			
			// allow installation to take up to 30 minutes (1.800 seconds)
			process.waitForOrKill(1_800_000)
			if (process.exitValue()) {
				error "Sprinkle did not exit properly"
				
				// sprinkle did not exit properly, sth. must have went wrong
				return false
			}
		}catch(e) {
			if (!verbose) {
				error "Running the sprinkle installer failed: " + e
			}
			return false
		}
		println "Sprinkle installer successfully finished"
		return true
	}

}

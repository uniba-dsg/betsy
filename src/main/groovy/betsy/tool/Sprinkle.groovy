package betsy.tool;

import org.apache.commons.lang.SystemUtils;

public class Sprinkle {

	static def error(def msg) {
		println "ERROR >> ${msg}"
	}

	def installerHomeDir

	Sprinkle(def installerHomeDir) {
		this.installerHomeDir = Objects.requireNonNull(installerHomeDir)
	}

	def isInstalled() {
		// Test if sprinkle is available
		try {
			def sout = new StringBuffer()
			def serr = new StringBuffer()
			def process
			if(SystemUtils.IS_OS_WINDOWS) {
				process = "cmd /C sprinkle -v".execute()
			}else {
				process = "sprinkle -v".execute()
			}
			process.consumeProcessOutput(sout, serr)
			process.waitForOrKill(10_000)
			if (process.exitValue()) {
				// unknown error, nevertheless sprinkle is most likely not usable
				error 'Sprinkle: >> ' + serr
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
			def sout = new StringBuffer()
			def serr = new StringBuffer()
			def command
			if(SystemUtils.IS_OS_WINDOWS) {
				command = "cmd /C sprinkle -v -s ${engineName}_server.rb"
			}else {
				command = "sprinkle -v -s ${engineName}_server.rb"
			}
			def process = command.execute(null, new File(installerHomeDir))
			process.consumeProcessOutput(System.out, System.err)
			// allow installation to take up to 30 minutes (1.800 seconds)
			process.waitForOrKill(1_800_000)
			if (process.exitValue()) {
				error "Sprinkle did not exit properly"
				// sprinkle did not exit properly, sth. must have went wrong
				return false
			}
		}catch(e) {
			error "Running the sprinkle installer failed: " + e
			return false
		}
		println "Sprinkle installer successfully finished"
		return true
	}

}

package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Process
import betsy.data.engines.Engine;
import betsy.data.engines.LocalEngines;
import betsy.executables.CompositeSequential
import configuration.processes.Processes

import java.awt.Desktop

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualizedEngines;
import de.uniba.wiai.dsg.betsy.virtual.host.exceptions.ConfigurationException;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.VirtualBoxWebService;

class TestUsingParameters {

	public static void main(String[] args) {
		Configuration config = Configuration.getInstance();

		CliBuilder cli = new CliBuilder(usage: "[options] <engines> <process>")
		cli.s(longOpt:'skip-reinstallation',"skip reinstalling each engine for each process")
		cli.o("Opens results in default browser")
		cli.h("Print out usage information")
		cli.p(args:1, argName:'ip-and-port', "Partner IP and Port (defaults to ${config.getValueAsString('PARTNER_IP_AND_PORT')} for standard engines)")
		cli.v("Use virtualized testing. Requires working VirtualBox installation, specified in 'Config.groovy'")
		// TODO implement parameter to clean all VirtualEngine directories (use VirtualEngine.cleanAll())

		def options = cli.parse(args)
		if (options == null || options == false || options.h) {
			println cli.usage()
			return
		}

		if (options.s) {
			println "Skipping reinstallation of engine for each process test"
		} else {
			println "Reinstalling engine per process test"
		}

		if (options.p){
			println "Setting Partner IP and Port to ${options.p} from previous setting ${config.getValueAsString('PARTNER_IP_AND_PORT')}"
			config.setValue("PARTNER_IP_AND_PORT", options.p);
		}

		try {
			List<Object> engines = null;
			List<Process> processes = null;
			try {
				if(options.v) {
					verifyVirtualizedTestingConfiguration()
					// start now and stop after tests are finished
					triggerVirtualBoxWebService()

					engines = parseVirtualizedEngines(options.arguments() as String[]).unique()
					println "Virtualized-Engines: ${engines.collect {it.name}}"
				}else {
					// default testing
					engines = parseLocalEngines(options.arguments() as String[]).unique()
					println "Local-Engines: ${engines.collect {it.name}}"
				}
				processes = parseProcesses(options.arguments() as String[]).unique()
			} catch (Exception e) {
	            println "----------------------"
	            println "ERROR - ${e.message} - Did you misspell the name?"
	            System.exit(0)
	        }

			println "Processes: ${processes.size() < 10 ? processes.collect {it.bpelFileNameWithoutExtension} : processes.size()}"

			if (options.s) {
				new Betsy(engines: engines, processes: processes).execute()
			} else {
				new Betsy(engines: engines, processes: processes, composite: new CompositeSequential()).execute()
			}

			if (options.o) {
				try {
					Desktop.getDesktop().browse(new File("test/reports/results.html").toURI())
				} catch (Exception e){
					// ignore any exceptions
				}
			}
		} catch (Throwable e) {
			println "----------------------"
			println "ERROR - ${e.message}"
		} finally {
			// shutdown as SoapUI creates threads which cannot be shutdown so easily
			System.exit(0)
		}
	}

	private static List<Engine> parseLocalEngines(String[] args) {
		if (args.length == 0 || "all" == args[0].toLowerCase()) {
			LocalEngines.availableEngines()
		} else {
			LocalEngines.build(args[0].toLowerCase().split(",") as List<String>)
		}
	}

	private static List<Engine> parseVirtualizedEngines(String[] args) {
		if (args.length == 0 || "all" == args[0].toLowerCase()) {
			VirtualizedEngines.availableEngines()
		} else {
			VirtualizedEngines.build(args[0].toLowerCase().split(",") as List<String>)
		}
	}

	private static List<Process> parseProcesses(String[] args) {
		if (args.length <= 1) {
			["ALL"].collect() { new Processes().get(it) }.flatten()
		} else {
			args[1].split(",").collect() { new Processes().get(it) }.flatten()
		}
	}

	private static void verifyVirtualizedTestingConfiguration() {
		Configuration config = Configuration.getInstance()
		String vboxPath = config.getValueAsString("virtualisation.vbox.path");
		String vboxwebsrvPath = config.getValueAsString("virtualisation.vbox.vboxwebsrv");
		String vboxManagePath = config.getValueAsString("virtualisation.vbox.vboxmanage");

		if(vboxwebsrvPath == null) {
			throw new ConfigurationException("Please specify a path to the VBoxWebSrv executable in the 'Config.groovy' file located in the project's root.")
		}

		File vboxwebsrvFile = new File(vboxPath, vboxwebsrvPath)
		if(!vboxwebsrvFile.exists()) {
			throw new ConfigurationException("The VBoxWebSrv executable specified in the 'Config.groovy' could not be found.")
		}

		if(vboxManagePath == null) {
			throw new ConfigurationException("Please specify a path to the VBoxManage executable in the 'Config.groovy' file located in the project's root.")
		}
		File vboxManageFile = new File(vboxPath, vboxManagePath)
		if(!vboxManageFile.exists()) {
			throw new ConfigurationException("The VBoxManage executable specified in the 'Config.groovy' could not be found.")
		}
	}

	private static void triggerVirtualBoxWebService() {
		Configuration config = Configuration.getInstance()
		String vboxPath = config.getValueAsString("virtualisation.vbox.path");
		String vboxwebsrvPath = config.getValueAsString("virtualisation.vbox.vboxwebsrv");

		File vboxwebsrvFile = new File(vboxPath, vboxwebsrvPath)
		final VirtualBoxWebService vBoxWebSrv = new VirtualBoxWebService(vboxwebsrvFile.absolutePath)
		vBoxWebSrv.start()

		Thread shutdownThread = new Thread({vBoxWebSrv.stop()} as Runnable)
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}

}

package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Process
import betsy.data.engines.Engine;
import betsy.data.engines.LocalEngines
import betsy.executables.CompositeSequential
import betsy.util.IPAddress
import configuration.processes.Processes

import java.awt.Desktop

import de.uniba.wiai.dsg.betsy.Configuration;
import de.uniba.wiai.dsg.betsy.virtual.host.VBoxConfiguration;
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualizedEngine
import de.uniba.wiai.dsg.betsy.virtual.host.VirtualizedEngines;
import de.uniba.wiai.dsg.betsy.virtual.host.utils.VBoxWebService;

class TestUsingParameters {

	public static void main(String[] args) {
		Configuration config = Configuration.getInstance();

		CliBuilder cli = new CliBuilder(usage: "[options] <engines> <process>")
		cli.s(longOpt:'skip-reinstallation',"skip reinstalling each engine for each process")
		cli.o("Opens results in default browser")
		cli.h("Print out usage information")
		cli.p(args:1, argName:'ip-and-port', "Partner IP and Port (defaults to ${IPAddress.getLocalAddress()} for standard engines)")
		cli.v("Use virtualized engines only. Requires working VirtualBox installation, specified in 'Config.groovy'")
		cli.l("Use local engines only")

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
			println "Setting Partner IP and Port to ${options.p} from previous setting ${IPAddress.getLocalAddress()}:2000"
			config.setValue("PARTNER_IP_AND_PORT", options.p);
		}else {
			println "Setting Partner IP and Port to ${IPAddress.getLocalAddress()}"
			config.setValue("PARTNER_IP_AND_PORT", IPAddress.getLocalAddress()+":2000");
		}


		List<Engine> engines = null
		List<Process> processes = null
		try {
			engines = parseEngines(options, options.arguments() as String[]).unique()
			processes = parseProcesses(options.arguments() as String[]).unique()
		} catch (Exception e) {
			println "----------------------"
			println "ERROR - ${e.message} - Did you misspell the name?"
			System.exit(0)
		}

		println "Engines: ${engines.collect {it.name}}"
		println "Processes: ${processes.size() < 10 ? processes.collect {it.bpelFileNameWithoutExtension} : processes.size()}"

		for(Engine engine : engines) {
			if(engine instanceof VirtualizedEngine) {
				// verify all mandatory config options
				new VBoxConfiguration().verify()
				VBoxWebService.instance.install()
				// do this only once
				break
			}
		}

		try {
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

	private static List<Engine> parseEngines(def options, String[] args) {
		if (args.length == 0 || "all" == args[0].toLowerCase()) {
			if(options.l) {
				return LocalEngines.availableEngines()
			}else if(options.v) {
				return VirtualizedEngines.availableEngines()
			}else {
				return VirtualizedEngines.availableEngines()
				+ LocalEngines.availableEngines()
			}
		} else {
			if(options.l) {
				return LocalEngines.build(args[0].toLowerCase().split(",") as List<String>)
			}else if(options.v) {
				return VirtualizedEngines.build(args[0].toLowerCase().split(",") as List<String>)
			}else {
				return VirtualizedEngines.build(args[0].toLowerCase().split(",") as List<String>)
				+ LocalEngines.build(args[0].toLowerCase().split(",") as List<String>)
			}
		}
	}

	private static List<Process> parseProcesses(String[] args) {
		if (args.length <= 1) {
			["ALL"].collect() { new Processes().get(it) }.flatten()
		} else {
			args[1].split(",").collect() { new Processes().get(it) }.flatten()
		}
	}
}

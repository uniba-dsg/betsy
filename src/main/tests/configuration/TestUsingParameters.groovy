package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Process
import betsy.data.engines.Engine;
import betsy.data.engines.LocalEngines
import betsy.executables.CompositeSequential
import betsy.virtual.host.VirtualBox
import betsy.virtual.host.virtualbox.VBoxConfiguration
import betsy.virtual.host.engines.VirtualizedEngine
import betsy.virtual.host.engines.VirtualizedEngines
import betsy.virtual.host.virtualbox.VBoxWebService
import betsy.virtual.host.virtualbox.VirtualBoxImpl
import configuration.processes.Processes

import java.awt.Desktop

class TestUsingParameters {

	public static void main(String[] args) {
		Configuration config = Configuration.getInstance();

		CliBuilder cli = new CliBuilder(usage: "[options] <engines> <process>")
		cli.s(longOpt:'skip-reinstallation',"skip reinstalling each engine for each process")
		cli.o("Opens results in default browser")
		cli.h("Print out usage information")
		cli.p(args:1, argName:'ip-and-port', "Partner IP and Port (defaults to ${config.getValue('PARTNER_IP_AND_PORT')} for standard engines)")

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
			println "Setting Partner IP and Port to ${options.p} from previous setting ${config.getValue('PARTNER_IP_AND_PORT')}"
			config.setValue("PARTNER_IP_AND_PORT", options.p);
		}

		List<Engine> engines = null
		List<Process> processes = null
		try {
			engines = parseEngines(options.arguments() as String[]).unique()
			processes = parseProcesses(options.arguments() as String[]).unique()
		} catch (Exception e) {
			println "----------------------"
			println "ERROR - ${e.message} - Did you misspell the name?"
			System.exit(0)
		}

		println "Engines: ${engines.collect {it.name}}"
		println "Processes: ${processes.size() < 10 ? processes.collect {it.bpelFileNameWithoutExtension} : processes.size()}"

		if (engines.any { it instanceof VirtualizedEngine}) {
			// verify IP set
			String partner = config.getValue('PARTNER_IP_AND_PORT')
			if(partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
				throw new IllegalStateException("VirtualizedEngines require your local IP-Address to be set. This can either be done via the -p option or directly in the Config.groovy file.")
			}
			
			// verify all mandatory config options
			new VBoxConfiguration().verify()
			new VBoxWebService().startAndInstall()

            VirtualBox vb = new VirtualBoxImpl()
            engines.each {
                if(it instanceof VirtualizedEngine) {
                    it.virtualBox = vb
                }
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

	private static List<Engine> parseEngines(String[] args) {
		if(args.length == 0){
			// local engines are default
			return LocalEngines.availableEngines()
		}

		if ("all" == args[0].toLowerCase()) {
			VirtualizedEngines.availableEngines() + LocalEngines.availableEngines()
		} else if ("vms" == args[0].toLowerCase()){
			VirtualizedEngines.availableEngines()
		} else if ("locals" == args[0].toLowerCase()) {
			LocalEngines.availableEngines()
		} else {
			List<String> engineNames = args[0].toLowerCase().split(",") as List<String>
			List<Engine> all = []
			
			for(String name : engineNames) {
				try {
					all.add(LocalEngines.build(name))
					continue
				}catch(IllegalArgumentException exception) {
					//ignore
				}
				
				try {
					all.add(VirtualizedEngines.build(name))
					continue
				}catch(IllegalArgumentException exception) {
					//ignore
				}
				
				throw new IllegalArgumentException("passed engine '${name}' does not exist, neither as local, nor as virtualized engine")
			}
			return all
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

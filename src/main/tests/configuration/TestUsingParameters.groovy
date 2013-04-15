package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Engine
import betsy.data.Engines
import betsy.data.Process
import betsy.executables.CompositeSequential
import configuration.processes.Processes

import java.awt.Desktop

class TestUsingParameters {

    public static void main(String[] args) {
        CliBuilder cli = new CliBuilder(usage: "[options] <engines> <process>")
        cli.s(longOpt: 'skip-reinstallation', "skip reinstalling each engine for each process")
        cli.o(longOpt: 'open-results-in-browser', "Opens results in default browser")
        cli.h(longOpt: 'help', "Print out usage information")
        cli.p(longOpt: 'partner-address', args: 1, argName: 'ip-and-port', "Partner IP and Port (defaults to ${Configuration.PARTNER_IP_AND_PORT})")

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

        if (options.p) {
            println "Setting Partner IP and Port to ${options.p} from previous setting ${Configuration.PARTNER_IP_AND_PORT}"
            Configuration.PARTNER_IP_AND_PORT = options.p
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

        try {
            if (options.s) {
                new Betsy(engines: engines, processes: processes).execute()
            } else {
                new Betsy(engines: engines, processes: processes, composite: new CompositeSequential()).execute()
            }

            if (options.o) {
                try {
                    Desktop.getDesktop().browse(new File("test/reports/results.html").toURI())
                } catch (Exception e) {
                    // ignore any exceptions
                }
            }
        } catch (Exception e) {
            println "----------------------"
            println "ERROR - ${e.message}"
        } finally {
            // shutdown as SoapUI creates threads which cannot be shutdown so easily
            System.exit(0)
        }
    }

    private static List<Engine> parseEngines(String[] args) {
        if (args.length == 0 || "all" == args[0].toLowerCase()) {
            Engines.availableEngines()
        } else {
            Engines.build(args[0].toLowerCase().split(",") as List<String>)
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

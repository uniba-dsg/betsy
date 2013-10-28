package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Engine
import betsy.data.engines.Engines
import betsy.data.BetsyProcess
import betsy.data.TestCase
import configuration.processes.Processes

import java.awt.*
import java.util.List

class TestUsingParameters {

    public static void main(String[] args) {
        // parsing cli params
        CliParser parser = new CliParser()
        parser.parse(args)

        // usage information if required
        if (parser.showUsage()) {
            println parser.usage()
            System.exit(0)
        }

        // setting partner address
        if (parser.hasCustomPartnerAddress()) {
            println "Setting Partner IP and Port to ${parser.getCustomPartnerAddress()} from previous setting ${Configuration.config.PARTNER_IP_AND_PORT}"
            Configuration.config.PARTNER_IP_AND_PORT = parser.getCustomPartnerAddress()
        }

        // parsing processes and engines
        List<Engine> engines = null
        List<BetsyProcess> processes = null
        try {
            engines = parseEngines(parser.arguments()).unique()
            processes = parseProcesses(parser.arguments()).unique()
        } catch (Exception e) {
            println "----------------------"
            println "ERROR - ${e.message} - Did you misspell the name?"
            e.printStackTrace()
            System.exit(0)
        }

        // print selection of engines and processes
        println "Engines: ${engines.collect { it.name }}"
        println "Processes: ${processes.size() < 10 ? processes.collect { it.bpelFileNameWithoutExtension } : processes.size()}"


        if (parser.checkDeployment()) {
            // check only whether the processes can be deployed
            processes.each { process ->
                process.testCases = [new TestCase().checkDeployment()]
            }
        }

        Betsy betsy = new Betsy(engines: engines, processes: processes)

        try {
            // execute
            try {
                betsy.execute()
            } catch (Exception e) {
                println "----------------------"
                println "ERROR - ${e.message}"
            }

            // open results in browser
            if (parser.openResultsInBrowser()) {
                try {
                    Desktop.getDesktop().browse(new File("test/reports/results.html").toURI())
                } catch (Exception ignore) {
                    // ignore any exceptions
                }
            }

        } finally {
            // shutdown as SoapUI creates threads which cannot be shutdown so easily
            // WARNING when a class is not found in soapUI, the corresponding exception does not show up.
            // SOLUTION remove exit line for testing purposes
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

    private static List<BetsyProcess> parseProcesses(String[] args) {
        if (args.length <= 1) {
            ["ALL"].collect() { new Processes().get(it) }.flatten()
        } else {
            args[1].split(",").collect() { new Processes().get(it) }.flatten()
        }
    }

}

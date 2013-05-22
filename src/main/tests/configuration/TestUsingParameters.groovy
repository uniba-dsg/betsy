package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Engine
import betsy.data.Engines
import betsy.data.Process
import betsy.data.TestCase
import betsy.executables.Composite
import betsy.executables.CompositeSequential
import configuration.processes.Processes
import org.codehaus.groovy.runtime.StackTraceUtils

import java.awt.Desktop

class TestUsingParameters {

    public static void main(String[] args) {
        // parsing cli params
        CliParser parser = new CliParser()
        parser.parse(args)

        // usage information if required
        if(parser.showUsage()){
            println parser.usage()
            System.exit(0)
        }

        // setting partner address
        if (parser.hasCustomPartnerAddress()) {
            println "Setting Partner IP and Port to ${parser.getCustomPartnerAddress()} from previous setting ${Configuration.PARTNER_IP_AND_PORT}"
            Configuration.PARTNER_IP_AND_PORT = parser.getCustomPartnerAddress()
        }

        // parsing processes and engines
        List<Engine> engines = null
        List<Process> processes = null
        try {
            engines = parseEngines(parser.arguments()).unique()
            processes = parseProcesses(parser.arguments()).unique()
        } catch (Exception e) {
            println "----------------------"
            println "ERROR - ${e.message} - Did you misspell the name?"
            System.exit(0)
        }

        // print selection of engines and processes
        println "Engines: ${engines.collect {it.name}}"
        println "Processes: ${processes.size() < 10 ? processes.collect {it.bpelFileNameWithoutExtension} : processes.size()}"


        if(parser.checkDeployment()) {
            // check only whether the processes can be deployed
            processes.each { process ->
                process.testCases = [new TestCase(onlyDeploymentCheck: true)]
            }
        }

        Betsy betsy = new Betsy(engines: engines, processes: processes)

        if (parser.skipReinstallation()) {
            println "Skipping reinstallation of engine for each process test"
            betsy.composite = new Composite()
        }

        try {

            // execute
            try{
                betsy.execute()
            } catch (Exception e) {
                println "----------------------"
                println "ERROR - ${e.message}"
            }

            // open results in browser
            if (parser.openResultsInBrowser()) {
                try {
                    Desktop.getDesktop().browse(new File("test/reports/results.html").toURI())
                } catch (Exception e) {
                    // ignore any exceptions
                }
            }

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

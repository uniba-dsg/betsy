package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.data.Process
import betsy.data.TestCase
import betsy.data.engines.Engine
import betsy.data.engines.LocalEngines
import betsy.virtual.host.VirtualBox
import betsy.virtual.host.engines.VirtualizedEngine
import betsy.virtual.host.engines.VirtualizedEngines
import betsy.virtual.host.virtualbox.VBoxConfiguration
import betsy.virtual.host.virtualbox.VBoxWebService
import betsy.virtual.host.virtualbox.VirtualBoxImpl
import configuration.processes.Processes

import java.awt.*
import java.util.List

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
            println "Setting Partner IP and Port to ${parser.getCustomPartnerAddress()} from previous setting ${Configuration.getInstance().getValue("PARTNER_IP_AND_PORT")}"
            Configuration.getInstance().setValue("PARTNER_IP_AND_PORT", parser.getCustomPartnerAddress());
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
            e.printStackTrace()
            System.exit(0)
        }

        // print selection of engines and processes
        println "Engines: ${engines.collect {it.name}}"
        println "Processes: ${processes.size() < 10 ? processes.collect {it.bpelFileNameWithoutExtension} : processes.size()}"


        if(parser.checkDeployment()) {
            // check only whether the processes can be deployed
            processes.each { process ->
                process.testCases = [new TestCase().checkDeployment()]
            }
        }

        if (engines.any { it instanceof VirtualizedEngine}) {
            // verify IP set
            String partner = Configuration.getInstance().getValue('PARTNER_IP_AND_PORT')
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

        Betsy betsy = new Betsy(engines: engines, processes: processes)

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
                } catch (Exception ignore) {
                    // ignore any exceptions
                }
            }

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
                }catch(IllegalArgumentException ignore) {
                    //ignore
                }

                try {
                    all.add(VirtualizedEngines.build(name))
                    continue
                }catch(IllegalArgumentException ignore) {
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

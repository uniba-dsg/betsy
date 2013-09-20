package configuration

import betsy.Betsy
import betsy.Configuration
import betsy.corebpel.CoreBPELEngineExtension
import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.data.engines.Engine
import betsy.data.engines.LocalEngine
import betsy.virtual.host.VirtualBox
import betsy.virtual.host.engines.VirtualizedEngine
import betsy.virtual.host.virtualbox.VBoxConfiguration
import betsy.virtual.host.virtualbox.VBoxWebService
import betsy.virtual.host.virtualbox.VirtualBoxImpl
import configuration.cli.CliParser
import configuration.cli.EngineParser
import configuration.cli.ProcessParser

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
            println "Setting Partner IP and Port to ${parser.getCustomPartnerAddress()} from previous setting ${Configuration.getInstance().getValue("PARTNER_IP_AND_PORT")}"
            Configuration.getInstance().setValue("PARTNER_IP_AND_PORT", parser.getCustomPartnerAddress());
        }

        // parsing processes and engines
        List<Engine> engines = null
        List<BetsyProcess> processes = null
        try {
            engines = new EngineParser(args: parser.arguments()).parse()
            processes = new ProcessParser(args: parser.arguments()).parse()
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

        if (parser.transformToCoreBpel()) {

            String transformations = parser.getCoreBPELTransformations()

            if (transformations == "ALL") {
                for (Engine engine : engines) {
                    if (engine instanceof VirtualizedEngine) {
                        CoreBPELEngineExtension.extendEngine(engine.defaultEngine)
                    } else if (engine instanceof LocalEngine) {
                        CoreBPELEngineExtension.extendEngine(engine)
                    }
                }
            } else if (transformations == "NONE") {
                // do nothing - default value
            } else {
                String[] xsls = transformations.split(",")

                for (Engine engine : engines) {
                    if (engine instanceof VirtualizedEngine) {
                        CoreBPELEngineExtension.extendEngine(engine.defaultEngine, xsls)
                    } else if (engine instanceof LocalEngine) {
                        CoreBPELEngineExtension.extendEngine(engine, xsls)
                    }
                }
            }

        }

        if (engines.any { it instanceof VirtualizedEngine }) {
            // verify IP set
            String partner = Configuration.getInstance().getValue('PARTNER_IP_AND_PORT')
            if (partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
                throw new IllegalStateException("VirtualizedEngines require your local IP-Address to be set. This can either be done via the -p option or directly in the Config.groovy file.")
            }

            // verify all mandatory config options
            new VBoxConfiguration().verify()
            new VBoxWebService().startAndInstall()

            VirtualBox vb = new VirtualBoxImpl()
            engines.each {
                if (it instanceof VirtualizedEngine) {
                    it.virtualBox = vb
                }
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
            System.exit(0)
        }
    }

}

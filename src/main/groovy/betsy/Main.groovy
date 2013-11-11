package betsy

import betsy.cli.CliParser
import betsy.cli.EngineParser
import betsy.cli.ProcessParser
import betsy.corebpel.CoreBPELEngineExtension
import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.data.engines.Engine
import betsy.data.engines.LocalEngine
import betsy.virtual.host.VirtualBox
import betsy.virtual.host.engines.VirtualEngine
import betsy.virtual.host.virtualbox.VBoxConfiguration
import betsy.virtual.host.virtualbox.VBoxWebService
import betsy.virtual.host.virtualbox.VirtualBoxImpl
import com.eviware.soapui.SoapUI

import java.awt.*
import java.util.List
import java.util.concurrent.TimeUnit

class Main {

    public static void main(String[] args) {
        // parsing cli params
        CliParser parser = new CliParser()
        parser.parse(args)

        // usage information if required
        if (parser.showUsage()) {
            println parser.usage()
            System.exit(0)
        }

        customPartnerAddress(parser)

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

        printSelectedEnginesAndProcesses(engines, processes)

        checkDeployment(parser, processes)
        coreBpel(parser, engines)
        virtualEngines(engines)

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
            System.exit(0);
        }
    }

    protected static void printSelectedEnginesAndProcesses(List<Engine> engines, List<BetsyProcess> processes) {
        // print selection of engines and processes
        println "Engines: ${engines.collect { it.name }}"
        println "Processes: ${processes.size() < 10 ? processes.collect { it.name } : processes.size()}"
    }

    protected static void customPartnerAddress(CliParser parser) {
        // setting partner address
        if (parser.hasCustomPartnerAddress()) {
            println "Setting Partner IP and Port to ${parser.getCustomPartnerAddress()} from previous setting ${Configuration.get("partner.ipAndPort")}"
            Configuration.set("partner.ipAndPort", parser.getCustomPartnerAddress())
        }
    }

    protected static void checkDeployment(CliParser parser, List<BetsyProcess> processes) {
        if (parser.checkDeployment()) {
            // check only whether the processes can be deployed
            processes.each { process ->
                process.testCases = [new TestCase().checkDeployment()]
            }
        }
    }

    protected static void virtualEngines(List<Engine> engines) {
        if (engines.any { it instanceof VirtualEngine }) {
            // verify IP set
            String partner = Configuration.get("partner.ipAndPort")
            if (partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
                throw new IllegalStateException("VirtualizedEngines require your local IP-Address to be set. " +
                        "This can either be done via the -p option or directly in the Config.groovy file.")
            }

            // verify all mandatory config options
            new VBoxConfiguration().verify()
            new VBoxWebService().startAndInstall()

            VirtualBox vb = new VirtualBoxImpl()
            engines.each {
                if (it instanceof VirtualEngine) {
                    it.virtualBox = vb
                }
            }
        }
    }

    protected static void coreBpel(CliParser parser, List<Engine> engines) {
        if (parser.transformToCoreBpel()) {

            String transformations = parser.getCoreBPELTransformations()

            if (transformations == "ALL") {
                for (Engine engine : engines) {
                    if (engine instanceof VirtualEngine) {
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
                    if (engine instanceof VirtualEngine) {
                        CoreBPELEngineExtension.extendEngine(engine.defaultEngine, xsls)
                    } else if (engine instanceof LocalEngine) {
                        CoreBPELEngineExtension.extendEngine(engine, xsls)
                    }
                }
            }

        }
    }

}

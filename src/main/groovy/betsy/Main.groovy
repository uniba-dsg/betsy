package betsy

import betsy.cli.CliParser
import betsy.cli.EngineParser
import betsy.cli.ProcessParser
import betsy.config.Configuration;
import betsy.corebpel.CoreBPELEngineExtension
import betsy.data.BetsyProcess
import betsy.data.TestCase
import betsy.data.engines.Engine
import betsy.data.engines.LocalEngine
import betsy.executables.ws.ExternalTestPartnerService
import betsy.virtual.host.VirtualBox
import betsy.virtual.host.engines.VirtualEngine
import betsy.virtual.host.virtualbox.VBoxConfiguration
import betsy.virtual.host.virtualbox.VBoxWebService
import betsy.virtual.host.virtualbox.VirtualBoxImpl
import com.sun.xml.internal.ws.server.ServerRtException
import corebpel.CoreBPEL
import org.apache.log4j.Logger
import org.apache.log4j.xml.DOMConfigurator
import org.codehaus.groovy.runtime.StackTraceUtils

import java.awt.*
import java.nio.file.Paths
import java.util.List

class Main {

    private static final Logger log = Logger.getLogger(Main.class)

    public static void main(String[] args) {
        activateLogging();

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
        } catch (IllegalArgumentException e) {
            println "----------------------"
            println "ERROR - ${e.message} - Did you misspell the name?"
            System.exit(0)
        }

        try {
            printSelectedEnginesAndProcesses(engines, processes)
            localhostPartnerAddressNotAllowedForTestingVirtualEngines(engines)

            Betsy betsy = new Betsy()
            useExternalPartnerService(parser, betsy)
            testBindabilityOfInternalPartnerService(parser, betsy)

            checkDeployment(parser, processes)
            coreBpel(parser, engines)
            virtualEngines(engines)

            onlyBuildSteps(parser, betsy)

            betsy.processes = processes
            betsy.engines = engines

            // execute
            try {
                betsy.execute()
            } catch (Exception e) {
                Throwable cleanedException = StackTraceUtils.deepSanitize(e)
                log.error "something went wrong during execution", cleanedException
            }

            // open results in browser
            if (parser.openResultsInBrowser()) {
                try {
                    Desktop.getDesktop().browse(Paths.get("test/reports/results.html").toUri())
                } catch (Exception ignore) {
                    // ignore any exceptions
                }
            }

        } catch (Exception e) {
            Throwable cleanedException = StackTraceUtils.deepSanitize(e)
            log.error cleanedException.getMessage(), cleanedException
        } finally {
            // shutdown as SoapUI creates threads which cannot be shutdown so easily
            // WARNING when a class is not found in soapUI, the corresponding exception does not show up.
            // SOLUTION remove exit line for testing purposes
            Thread.sleep(3000);
            System.exit(0);
        }
    }

    static void onlyBuildSteps(CliParser cliParser, Betsy betsy) {
        if(cliParser.onlyBuildSteps()){
            betsy.composite = new betsy.executables.Composite() {
                @Override
                protected void testSoapUi(BetsyProcess process) {
                }

                @Override
                protected void collect(BetsyProcess process) {
                }

                @Override
                protected void test(BetsyProcess process) {
                }

                @Override
                protected void installAndStart(BetsyProcess process) {
                }

                @Override
                protected void deploy(BetsyProcess process) {
                }

                @Override
                protected void shutdown(BetsyProcess process) {
                }

                @Override
                protected createReports() {
                }
            }
        }
    }

    private static void localhostPartnerAddressNotAllowedForTestingVirtualEngines(List<Engine> engines) {
        if (engines.any { it instanceof VirtualEngine }) {
            // verify IP set
            String partner = Configuration.get("partner.ipAndPort")
            if (partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
                throw new IllegalStateException("Virtual engines require your local IP-Address to be set. " +
                        "This can either be done via the -p option or directly in the Config.groovy file.")
            }
        }
    }

    private static void testBindabilityOfInternalPartnerService(CliParser parser, Betsy betsy) {
        if (!parser.useExternalPartnerService()) {
            // test the correctness
            try {
                betsy.composite.testPartner.publish()
            } catch (ServerRtException e) {
                throw new IllegalStateException("the given partner address is not bindable for this system", e);
            } finally {
                betsy.composite.testPartner.unpublish()
            }
        }
    }

    private static void useExternalPartnerService(CliParser parser, Betsy betsy) {
        // do not use internal partner service
        if (parser.useExternalPartnerService()) {
            betsy.composite.testPartner = new ExternalTestPartnerService()
            betsy.composite.requestTimeout = 15 * 1000 // increase request timeout as invoking external service
        }
    }

    protected static String activateLogging() {

        // activate log4j logging
        DOMConfigurator.configure(Main.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml")
    }

    protected static void printSelectedEnginesAndProcesses(List<Engine> engines, List<BetsyProcess> processes) {
        // print selection of engines and processes
        log.info "Engines: ${engines.collect { it.name }}"
        log.info "Processes: ${processes.size() < 10 ? processes.collect { it.name } : processes.size()}"
    }

    protected static void customPartnerAddress(CliParser parser) {
        // setting partner address
        if (parser.hasCustomPartnerAddress()) {

            String newPartnerAddress = parser.getCustomPartnerAddress()
            if(!newPartnerAddress.contains(":")){
                throw new IllegalArgumentException("Port is missing in partner address [$newPartnerAddress]")
            }

            log.info "Setting Partner IP and Port to ${newPartnerAddress} from previous setting ${Configuration.get("partner.ipAndPort")}"
            Configuration.set("partner.ipAndPort", newPartnerAddress)
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
                        CoreBPELEngineExtension.extendEngine(engine.defaultEngine, CoreBPEL.ALL_OPTION)
                    } else if (engine instanceof LocalEngine) {
                        CoreBPELEngineExtension.extendEngine(engine, CoreBPEL.ALL_OPTION)
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

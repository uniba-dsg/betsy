package betsy.bpel;

import betsy.bpel.cli.CliParser;
import betsy.bpel.cli.EngineParser;
import betsy.bpel.cli.ProcessParser;
import betsy.common.config.Configuration;
import betsy.bpel.corebpel.CoreBPELEngineExtension;
import betsy.bpel.model.BetsyProcess;
import betsy.common.model.TestCase;
import betsy.bpel.engines.Engine;
import betsy.bpel.engines.LocalEngine;
import betsy.bpel.ws.TestPartnerServicePublisherExternal;
import betsy.bpel.virtual.host.VirtualBox;
import betsy.bpel.virtual.host.engines.VirtualEngine;
import betsy.bpel.virtual.host.virtualbox.VBoxWebService;
import betsy.bpel.virtual.host.virtualbox.VirtualBoxImpl;
import corebpel.CoreBPEL;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        activateLogging();

        // parsing cli params
        CliParser parser = new CliParser();
        parser.parse(args);

        // usage information if required
        if (parser.showUsage()) {
            parser.printUsage();
            System.exit(0);
        }


        customPartnerAddress(parser);

        // parsing processes and engines
        List<Engine> engines = null;
        List<BetsyProcess> processes = null;
        try {
            engines = new EngineParser(parser.arguments()).parse();
            processes = new ProcessParser(parser.arguments()).parse();
        } catch (IllegalArgumentException e) {
            System.out.println("----------------------");
            System.out.println("ERROR - " + e.getMessage() + " - Did you misspell the name?");
            System.exit(0);
        }


        try {
            printSelectedEnginesAndProcesses(engines, processes);
            localhostPartnerAddressNotAllowedForTestingVirtualEngines(engines);

            Betsy betsy = new Betsy();
            useExternalPartnerService(parser, betsy);
            testBindabilityOfInternalPartnerService(parser, betsy);

            checkDeployment(parser, processes);
            coreBpel(parser, engines);
            virtualEngines(engines);

            onlyBuildSteps(parser, betsy);

            betsy.setProcesses(processes);
            betsy.setEngines(engines);

            // execute
            try {
                betsy.execute();
            } catch (Exception e) {
                Throwable cleanedException = StackTraceUtils.deepSanitize(e);
                log.error("something went wrong during execution", cleanedException);
            }


            // open results in browser
            if (parser.openResultsInBrowser()) {
                try {
                    Desktop.getDesktop().browse(Paths.get("test/reports/results.html").toUri());
                } catch (Exception ignore) {
                    // ignore any exceptions
                }

            }


        } catch (Exception e) {
            Throwable cleanedException = StackTraceUtils.deepSanitize(e);
            log.error(cleanedException.getMessage(), cleanedException);
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // shutdown as SoapUI creates threads which cannot be shutdown so easily
        // WARNING when a class is not found in soapUI, the corresponding exception does not show up.
        // SOLUTION remove exit line for testing purposes
        System.exit(0);
    }

    public static void onlyBuildSteps(CliParser cliParser, Betsy betsy) {
        if (cliParser.onlyBuildSteps()) {
            betsy.setComposite(new Composite() {
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
                protected void createReports() {
                }

            });
        }

    }

    private static void localhostPartnerAddressNotAllowedForTestingVirtualEngines(List<Engine> engines) {
        if (usesVirtualEngines(engines)) {
            // verify IP set
            String partner = Configuration.get("partner.ipAndPort");
            if (partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
                throw new IllegalStateException("Virtual engines require your local IP-Address to be set. " + "This can either be done via the -p option or directly in the config.properties file.");
            }

        }

    }

    private static boolean usesVirtualEngines(List<Engine> engines) {
        return !getVirtualEngines(engines).isEmpty();
    }

    private static List<VirtualEngine> getVirtualEngines(List<Engine> engines) {
        return engines.stream().filter(e -> e instanceof VirtualEngine).map(e -> (VirtualEngine) e).collect(Collectors.toList());
    }

    private static void testBindabilityOfInternalPartnerService(CliParser parser, Betsy betsy) {
        if (!parser.useExternalPartnerService()) {
            // test the correctness
            try {
                betsy.getComposite().getTestPartner().publish();
            } catch (Exception e) {
                throw new IllegalStateException("the given partner address is not bindable for this system", e);
            } finally {
                betsy.getComposite().getTestPartner().unpublish();
            }
        }
    }

    private static void useExternalPartnerService(CliParser parser, Betsy betsy) {
        // do not use internal partner service
        if (parser.useExternalPartnerService()) {
            betsy.getComposite().setTestPartner(new TestPartnerServicePublisherExternal());
            betsy.getComposite().setRequestTimeout(15 * 1000);// increase request timeout as invoking external service
        }
    }

    protected static String activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(Main.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        return System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml");
    }

    protected static void printSelectedEnginesAndProcesses(List<Engine> engines, final List<BetsyProcess> processes) {
        // print selection of engines and processes
        log.info("Engines " + engines.size() + ": " + engines.stream().map(Engine::getName).collect(Collectors.joining(", ")));
        log.info("Processes " + processes.size() + ": " + processes.stream().map(BetsyProcess::getName).collect(Collectors.joining(", ")));
    }

    protected static void customPartnerAddress(CliParser parser) {
        // setting partner address
        if (parser.hasCustomPartnerAddress()) {

            final String newPartnerAddress = parser.getCustomPartnerAddress();
            if (!newPartnerAddress.contains(":")) {
                throw new IllegalArgumentException("Port is missing in partner address [" + newPartnerAddress + "]");
            }

            log.info("Setting Partner IP and Port to " + newPartnerAddress + " from previous setting " + Configuration.get("partner.ipAndPort"));
            Configuration.setPartnerIpAndPort(newPartnerAddress);
        }

    }

    protected static void checkDeployment(CliParser parser, List<BetsyProcess> processes) {
        if (parser.checkDeployment()) {
            // check only whether the processes can be deployed
            for (BetsyProcess process : processes) {
                process.setTestCases(Arrays.asList(new TestCase().checkDeployment()));
            }

        }

    }

    protected static void virtualEngines(List<Engine> engines) {
        if (usesVirtualEngines(engines)) {
            // verify all mandatory config options
            try {
                new VBoxWebService().startAndInstall();
            } catch (IOException e) {
                throw new RuntimeException("Cannot start web service of vbox", e);
            }

            VirtualBox vb = new VirtualBoxImpl();
            for (VirtualEngine engine : getVirtualEngines(engines)) {
                engine.setVirtualBox(vb);
            }
        }

    }

    protected static void coreBpel(CliParser parser, List<Engine> engines) {
        if (parser.transformToCoreBpel()) {

            String transformations = parser.getCoreBPELTransformations();

            switch (transformations) {
                case "ALL":
                    for (Engine engine : engines) {
                        if (engine instanceof VirtualEngine) {
                            CoreBPELEngineExtension.extendEngine(((VirtualEngine) engine).defaultEngine, CoreBPEL.ALL_OPTION);
                        } else if (engine instanceof LocalEngine) {
                            CoreBPELEngineExtension.extendEngine(engine, CoreBPEL.ALL_OPTION);
                        }
                    }

                    break;
                case "NONE":
                    // do nothing - default value
                    break;
                default:
                    List<String> xsls = Arrays.asList(transformations.split(","));

                    for (Engine engine : engines) {
                        if (engine instanceof VirtualEngine) {
                            CoreBPELEngineExtension.extendEngine(((VirtualEngine) engine).defaultEngine, xsls);
                        } else if (engine instanceof LocalEngine) {
                            CoreBPELEngineExtension.extendEngine(engine, xsls);
                        }
                    }

                    break;
            }


        }

    }
}

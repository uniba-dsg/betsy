package betsy.bpel;

import betsy.bpel.cli.*;
import betsy.bpel.corebpel.CoreBPELEngineExtension;
import betsy.bpel.engines.Engine;
import betsy.bpel.engines.LocalEngine;
import betsy.bpel.model.BPELTestCase;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.virtual.host.VirtualBox;
import betsy.bpel.virtual.host.engines.VirtualEngine;
import betsy.bpel.virtual.host.virtualbox.VBoxWebService;
import betsy.bpel.virtual.host.virtualbox.VirtualBoxImpl;
import betsy.bpel.ws.TestPartnerServicePublisherExternal;
import betsy.common.config.Configuration;
import betsy.common.engines.Nameable;
import corebpel.CoreBPEL;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BPELMain {
    private static final Logger LOGGER = Logger.getLogger(BPELMain.class);

    public static void main(String[] args) {
        activateLogging();

        // parsing cli params
        BPELCliParser parser = new BPELCliParser(args);
        BPELCliParameter params = parser.parse();

        // usage information if required
        if (params.showHelp()) {
            parser.printUsage();
            return;
        }

        customPartnerAddress(params);

        try {
            printSelectedEnginesAndProcesses(params.getEngines(), params.getProcesses());
            localhostPartnerAddressNotAllowedForTestingVirtualEngines(params.getEngines());

            BPELBetsy betsy = new BPELBetsy();
            useExternalPartnerService(params, betsy);
            assertBindabilityOfInternalPartnerService(params, betsy);

            checkDeployment(params, params.getProcesses());
            coreBpel(params, params.getEngines());
            virtualEngines(params.getEngines());

            onlyBuildSteps(params, betsy);

            betsy.setProcesses(params.getProcesses());
            betsy.setEngines(params.getEngines());

            // execute
            try {
                betsy.execute();
            } catch (Exception e) {
                Throwable cleanedException = StackTraceUtils.deepSanitize(e);
                LOGGER.error("something went wrong during execution", cleanedException);
            }


            // open results in browser
            if (params.openResultsInBrowser()) {
                try {
                    Path htmlDashboard = Paths.get("test/reports/results.html");
                    Desktop.getDesktop().browse(htmlDashboard.toUri());
                } catch (Exception ignore) {
                    LOGGER.error("Could not start browser", ignore);
                }

            }


        } catch (Exception e) {
            Throwable cleanedException = StackTraceUtils.deepSanitize(e);
            LOGGER.error(cleanedException.getMessage(), cleanedException);
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

    public static void onlyBuildSteps(BPELCliParameter params, BPELBetsy betsy) {
        if (params.buildArtifactsOnly()) {
            betsy.setComposite(new BPELComposite() {
                @Override
                protected void testSoapUi(BPELProcess process) {
                }

                @Override
                protected void collect(BPELProcess process) {
                }

                @Override
                protected void test(BPELProcess process) {
                }

                @Override
                protected void installAndStart(BPELProcess process) {
                }

                @Override
                protected void deploy(BPELProcess process) {
                }

                @Override
                protected void shutdown(BPELProcess process) {
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

    private static void assertBindabilityOfInternalPartnerService(BPELCliParameter params, BPELBetsy betsy) {

        if (!params.useExternalPartnerService()) {
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

    private static void useExternalPartnerService(BPELCliParameter params, BPELBetsy betsy) {
        // do not use internal partner service
        if (params.useExternalPartnerService()) {
            betsy.getComposite().setTestPartner(new TestPartnerServicePublisherExternal());
            betsy.getComposite().setRequestTimeout(15 * 1000);// increase request timeout as invoking external service
        }
    }

    protected static void activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(BPELMain.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml");
    }

    protected static void printSelectedEnginesAndProcesses(List<Engine> engines, List<BPELProcess> processes) {
        // print selection of engines and processes
        LOGGER.info("Engines (" + engines.size() + "): " + Nameable.getNames(engines));
        LOGGER.info("Processes (" + processes.size() + "): " + Nameable.getNames(processes).stream().limit(10).collect(Collectors.toList()));
    }

    protected static void customPartnerAddress(BPELCliParameter params) {
        // setting partner address
        if (params.hasCustomPartnerAddress()) {

            final String newPartnerAddress = params.getCustomPartnerAddress();
            if (!newPartnerAddress.contains(":")) {
                throw new IllegalArgumentException("Port is missing in partner address [" + newPartnerAddress + "]");
            }

            LOGGER.info("Setting Partner IP and Port to " + newPartnerAddress + " from previous setting " + Configuration.get("partner.ipAndPort"));
            Configuration.setPartnerIpAndPort(newPartnerAddress);
        }

    }

    protected static void checkDeployment(BPELCliParameter params, List<BPELProcess> processes) {
        if (params.checkDeployment()) {
            // check only whether the processes can be deployed
            for (BPELProcess process : processes) {
                process.setTestCases(Arrays.asList(new BPELTestCase().checkDeployment()));
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

    protected static void coreBpel(BPELCliParameter params, List<Engine> engines) {
        if (params.transformToCoreBpel()) {

            String transformations = params.getCoreBPELTransformations();

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

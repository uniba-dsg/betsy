package betsy.bpel;

import betsy.bpel.cli.BPELCliParameter;
import betsy.bpel.cli.BPELCliParser;
import betsy.bpel.corebpel.CoreBPELEngineExtension;
import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.engines.AbstractLocalBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.bpel.soapui.SoapUIShutdownHelper;
import betsy.bpel.virtual.host.VirtualBox;
import betsy.bpel.virtual.host.engines.AbstractVirtualBPELEngine;
import betsy.bpel.virtual.host.virtualbox.VBoxWebService;
import betsy.bpel.virtual.host.virtualbox.VirtualBoxImpl;
import betsy.bpel.ws.TestPartnerServicePublisherExternal;
import betsy.common.HasName;
import betsy.common.config.Configuration;
import betsy.common.model.EngineIndependentProcess;
import corebpel.CoreBPEL;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BPELMain {

    private static final Logger LOGGER = Logger.getLogger(BPELMain.class);

    private static boolean shouldShutdownSoapUi = true;

    public static void main(String... args) {
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

            onlyBuildStepsOrUseInstalledEngine(params, betsy);

            betsy.setProcesses(params.getProcesses());
            betsy.setEngines(params.getEngines());
            betsy.setTestFolder(params.getTestFolderName());


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

        if (shouldShutdownSoapUi) {
            SoapUIShutdownHelper.shutdownSoapUIForReal();
        }
    }

    public static void shutdownSoapUiAfterCompletion(boolean shouldShutdown) {
        shouldShutdownSoapUi = shouldShutdown;
    }

    public static void onlyBuildStepsOrUseInstalledEngine(BPELCliParameter params, BPELBetsy betsy) {
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
                protected void install(BPELProcess process) {
                }

                @Override
                protected void startup(BPELProcess process) {
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
        } else {

            if (params.keepEngineRunning() && params.useInstalledEngine()) {
                betsy.setComposite(new BPELComposite() {

                    @Override
                    protected void shutdown(BPELProcess process) {
                        // is already installed - use existing installation
                    }

                    @Override
                    protected void install(BPELProcess process) {
                        // is already installed - use existing installation
                    }

                });
            } else if (params.useInstalledEngine()) {
                betsy.setComposite(new BPELComposite() {

                    @Override
                    protected void install(BPELProcess process) {
                        // is already installed - use existing installation
                    }

                });
            } else if (params.keepEngineRunning()) {
                betsy.setComposite(new BPELComposite() {

                    @Override
                    protected void shutdown(BPELProcess process) {
                        // is already installed - use existing installation
                    }

                });
            }
        }
    }

    private static void localhostPartnerAddressNotAllowedForTestingVirtualEngines(List<AbstractBPELEngine> engines) {
        if (usesVirtualEngines(engines)) {
            // verify IP set
            String partner = Configuration.get("partner.ipAndPort");
            if (partner.contains("0.0.0.0") || partner.contains("127.0.0.1")) {
                throw new IllegalStateException("Virtual engines require your local IP-Address to be set. " + "This can either be done via the -p option or directly in the config.properties file.");
            }

        }
    }

    private static boolean usesVirtualEngines(List<AbstractBPELEngine> engines) {
        return !getVirtualEngines(engines).isEmpty();
    }

    private static List<AbstractVirtualBPELEngine> getVirtualEngines(List<AbstractBPELEngine> engines) {
        return engines.stream().filter(e -> e instanceof AbstractVirtualBPELEngine).map(e -> (AbstractVirtualBPELEngine) e).collect(Collectors.toList());
    }

    private static void assertBindabilityOfInternalPartnerService(BPELCliParameter params, BPELBetsy betsy) {

        if (!params.useExternalPartnerService()) {
            // test the correctness
            try {
                betsy.getComposite().getTestingAPI().startup();
            } catch (Exception e) {
                throw new IllegalStateException("the given partner address is not bindable for this system", e);
            } finally {
                betsy.getComposite().getTestingAPI().shutdown();
            }
        }
    }

    private static void useExternalPartnerService(BPELCliParameter params, BPELBetsy betsy) {
        // do not use internal partner service
        if (params.useExternalPartnerService()) {
            betsy.getComposite().getTestingAPI().setTestPartner(new TestPartnerServicePublisherExternal());
            betsy.getComposite().getTestingAPI().setRequestTimeout(15_000);// increase request timeout as invoking external service
        }
    }

    protected static void activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(BPELMain.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml");
    }

    protected static void printSelectedEnginesAndProcesses(List<AbstractBPELEngine> engines, List<EngineIndependentProcess> processes) {
        // print selection of engines and processes
        LOGGER.info("Engines (" + engines.size() + "): " + HasName.getNames(engines));
        LOGGER.info("Processes (" + processes.size() + "): " + HasName.getNames(processes).stream().limit(10).collect(Collectors.toList()));
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

    protected static void checkDeployment(BPELCliParameter params, List<EngineIndependentProcess> processes) {
        if (params.checkDeployment()) {
            // check only whether the processes can be deployed
            for (int i = 0; i < processes.size(); i++) {
                EngineIndependentProcess process = processes.get(i);
                processes.set(i, process.withNewTestCases(Collections.singletonList(new BPELTestCase().checkDeployment())));
            }
        }
    }

    protected static void virtualEngines(List<AbstractBPELEngine> engines) {
        if (usesVirtualEngines(engines)) {
            // verify all mandatory config options
            try {
                new VBoxWebService().startAndInstall();
            } catch (IOException e) {
                throw new RuntimeException("Cannot start web service of vbox", e);
            }

            VirtualBox vb = new VirtualBoxImpl();
            for (AbstractVirtualBPELEngine engine : getVirtualEngines(engines)) {
                engine.setVirtualBox(vb);
            }
        }

    }

    protected static void coreBpel(BPELCliParameter params, List<AbstractBPELEngine> engines) {
        if (params.transformToCoreBpel()) {

            String transformations = params.getCoreBPELTransformations();

            switch (transformations) {
                case "ALL":
                    for (AbstractBPELEngine engine : engines) {
                        if (engine instanceof AbstractVirtualBPELEngine) {
                            CoreBPELEngineExtension.extendEngine(((AbstractVirtualBPELEngine) engine).defaultEngine, CoreBPEL.ALL_OPTION);
                        } else if (engine instanceof AbstractLocalBPELEngine) {
                            CoreBPELEngineExtension.extendEngine(engine, CoreBPEL.ALL_OPTION);
                        }
                    }

                    break;
                case "NONE":
                    // do nothing - default value
                    break;
                default:
                    List<String> xsls = Arrays.asList(transformations.split(","));

                    for (AbstractBPELEngine engine : engines) {
                        if (engine instanceof AbstractVirtualBPELEngine) {
                            CoreBPELEngineExtension.extendEngine(((AbstractVirtualBPELEngine) engine).defaultEngine, xsls);
                        } else if (engine instanceof AbstractLocalBPELEngine) {
                            CoreBPELEngineExtension.extendEngine(engine, xsls);
                        }
                    }

                    break;
            }

        }

    }
}

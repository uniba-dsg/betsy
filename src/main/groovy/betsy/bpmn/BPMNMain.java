package betsy.bpmn;

import betsy.bpmn.cli.*;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.HasName;
import betsy.common.model.input.EngineIndependentProcess;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNMain {
    public static void main(String... args) {
        activateLogging();

        // parsing cli params
        BPMNCliParser parser = new BPMNCliParser(args);
        BPMNCliParameter params = parser.parse();

        // usage information if required
        if (params.showHelp()) {
            parser.printUsage();
            return;
        }

        try {
            printSelectedEnginesAndProcesses(params.getEngines(), params.getProcesses());

            BPMNBetsy betsy = new BPMNBetsy();

            onlyBuildSteps(params, betsy);

            betsy.setEngines(params.getEngines());
            betsy.setProcesses(params.getProcesses());
            betsy.setTestFolder(params.getTestFolderName());

            if (params.keepEngineRunning() && params.useInstalledEngine()) {
                betsy.setComposite(new BPMNComposite() {

                    @Override
                    protected void shutdown(BPMNProcess process) {
                        // keep engine running - no shutdown!
                    }

                    @Override
                    protected void install(BPMNProcess process) {
                        // is already installed - use existing installation
                    }

                });
            } else if (params.useInstalledEngine()) {
                betsy.setComposite(new BPMNComposite() {

                    @Override
                    protected void install(BPMNProcess process) {
                        // is already installed - use existing installation
                    }

                });
            } else if (params.keepEngineRunning()) {
                betsy.setComposite(new BPMNComposite() {

                    @Override
                    protected void shutdown(BPMNProcess process) {
                        // keep engine running - no shutdown!
                    }

                });
            }

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
                    Desktop.getDesktop().browse(Paths.get("test/reports/results.html").toUri());
                } catch (Exception ignore) {
                    // ignore any exceptions
                }

            }

        } catch (Exception e) {
            Throwable cleanedException = StackTraceUtils.deepSanitize(e);
            LOGGER.error(cleanedException.getMessage(), cleanedException);
        }

    }

    protected static void activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(BPMNMain.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml");
    }

    protected static void printSelectedEnginesAndProcesses(List<AbstractBPMNEngine> engines, List<EngineIndependentProcess> processes) {
        // print selection of engines and processes
        LOGGER.info("Engines (" + engines.size() + "): " + HasName.getNames(engines));
        LOGGER.info("Processes (" + processes.size() + "): " + HasName.getNames(processes).stream().limit(10).collect(Collectors.toList()));
    }

    public static void onlyBuildSteps(BPMNCliParameter params, BPMNBetsy betsy) {
        if (params.buildArtifactsOnly()) {
            betsy.setComposite(new BPMNComposite() {
                @Override
                protected void collect(BPMNProcess process) {
                }

                @Override
                protected void test(BPMNProcess process) {
                }

                @Override
                protected void start(BPMNProcess process) {
                }

                @Override
                protected void deploy(BPMNProcess process) {
                }

                @Override
                protected void shutdown(BPMNProcess process) {
                }

                @Override
                protected void createReports() {
                }

            });
        }

    }

    private static final Logger LOGGER = Logger.getLogger(BPMNMain.class);
}

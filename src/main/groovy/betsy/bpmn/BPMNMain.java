package betsy.bpmn;

import betsy.bpel.Main;
import betsy.bpmn.cli.*;
import betsy.bpmn.engines.BPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.common.engines.Nameable;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.awt.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class BPMNMain {
    public static void main(String[] args) {
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

            // execute
            try {
                betsy.execute();
            } catch (Exception e) {
                Throwable cleanedException = StackTraceUtils.deepSanitize(e);
                log.error("something went wrong during execution", cleanedException);
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
            log.error(cleanedException.getMessage(), cleanedException);
        }

    }

    protected static String activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(Main.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        return System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml");
    }

    protected static void printSelectedEnginesAndProcesses(List<BPMNEngine> engines, List<BPMNProcess> processes) {
        // print selection of engines and processes
        log.info("Engines (" + engines.size() + "): " + Nameable.getNames(engines));
        log.info("Processes (" + processes.size() + "): " + Nameable.getNames(processes).stream().limit(10).collect(Collectors.toList()));
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
                protected void installAndStart(BPMNProcess process) {
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

    private static final Logger log = Logger.getLogger(BPMNMain.class);
}

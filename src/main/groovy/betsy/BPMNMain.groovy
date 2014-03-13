package betsy

import betsy.cli.BPMNCliParser
import betsy.cli.BPMNEngineParser
import betsy.cli.BPMNProcessParser
import betsy.data.BPMNProcess
import betsy.data.engines.BPMNEngine
import org.apache.log4j.Logger
import org.apache.log4j.xml.DOMConfigurator
import org.codehaus.groovy.runtime.StackTraceUtils

import java.awt.Desktop
import java.nio.file.Paths

class BPMNMain {

    private static final Logger log = Logger.getLogger(BPMNMain.class)

    public static void main(String[] args){
        activateLogging()

        // parsing cli params
        BPMNCliParser parser = new BPMNCliParser()
        parser.parse(args)

        // usage information if required
        if (parser.showUsage()) {
            println parser.usage()
            System.exit(0)
        }

        // parsing processes and engines
        List<BPMNEngine> engines = null
        List<BPMNProcess> processes = null
        try {
            engines = new BPMNEngineParser(args: parser.arguments()).parse()
            //processes = new BPMNProcessParser(args: parser.arguments()).parse()
            BPMNProcess process = new BPMNProcess(name: "XOR", group: "gateways", key: "XOR", groupId: "org.camunda.bpm.dsg", version: "1.0")
            //BPMNProcess process2 = new BPMNProcess(name: "simple", group: "tasks", key: "SimpleApplication", groupId: "org.camunda.bpm.dsg", version: "1.0")
            processes = new ArrayList<>()
            processes.add(process)
            //processes.add(process2)
        } catch (IllegalArgumentException e) {
            println "----------------------"
            println "ERROR - ${e.message} - Did you misspell the name?"
            System.exit(0)
        }

        try {
            printSelectedEnginesAndProcesses(engines, processes)

            BPMNBetsy betsy = new BPMNBetsy()

            onlyBuildSteps(parser, betsy)

            betsy.engines = engines
            betsy.processes = processes

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
            System.exit(0);
        }
    }

    protected static String activateLogging() {
        // activate log4j logging
        DOMConfigurator.configure(Main.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml")
    }

    protected static void printSelectedEnginesAndProcesses(List<BPMNEngine> engines, List<BPMNProcess> processes) {
        // print selection of engines and processes
        log.info "Engines: ${engines.collect { it.name }}"
        log.info "Processes: ${processes.size() < 10 ? processes.collect { it.name } : processes.size()}"
    }

    static void onlyBuildSteps(BPMNCliParser cliParser, Betsy betsy) {
        if(cliParser.onlyBuildSteps()){
            betsy.composite = new betsy.executables.BPMNComposite() {

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
                protected createReports() {
                }
            }
        }
    }
}

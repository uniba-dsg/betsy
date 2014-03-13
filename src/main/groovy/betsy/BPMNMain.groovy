package betsy

import betsy.cli.BPMNCliParser
import betsy.cli.BPMNEngineParser
import betsy.cli.BPMNProcessParser
import betsy.data.BPMNProcess
import betsy.data.engines.BPMNEngine
import org.apache.log4j.Logger
import org.apache.log4j.xml.DOMConfigurator

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
            processes = new BPMNProcessParser(args: parser.arguments()).parse()
        } catch (IllegalArgumentException e) {
            println "----------------------"
            println "ERROR - ${e.message} - Did you misspell the name?"
            System.exit(0)
        }
    }

    protected static String activateLogging() {

        // activate log4j logging
        DOMConfigurator.configure(Main.class.getResource("/log4j.xml"));

        // set log4j property to avoid conflicts with soapUIs -> effectly disabling soapUI's own logging
        System.setProperty("soapui.log4j.config", "src/main/resources/soapui-log4j.xml")
    }
}

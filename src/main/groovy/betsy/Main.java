package betsy;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import betsy.bpel.BPELMain;
import betsy.bpel.tools.PartnerServiceControlGUI;
import betsy.bpmn.BPMNMain;
import betsy.common.virtual.cbetsy.DockerMain;
import betsy.tools.AnalyticsMain;
import betsy.tools.CreateInitialDatabase;
import betsy.tools.EngineControlGUI;
import betsy.tools.EngineMain;
import betsy.tools.ProcessMain;
import betsy.tools.TestsPerGroup;
import betsy.tools.TimeoutCalibrator;
import org.xml.sax.SAXException;

public class Main {

    public static void main(String... args) throws JAXBException, IOException, SAXException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        if ("bpel".equalsIgnoreCase(args[0])) {
            BPELMain.main(createArgsWithoutFirstValue(args));
        } else if ("bpmn".equalsIgnoreCase(args[0])) {
            BPMNMain.main(createArgsWithoutFirstValue(args));
        } else if ("engine".equalsIgnoreCase(args[0])) {
            EngineMain.main(createArgsWithoutFirstValue(args));
        } else if ("process".equalsIgnoreCase(args[0])) {
            ProcessMain.main(createArgsWithoutFirstValue(args));
        } else if ("analytics".equalsIgnoreCase(args[0])) {
            AnalyticsMain.main(createArgsWithoutFirstValue(args));
        } else if ("enginecontrol".equalsIgnoreCase(args[0])) {
            EngineControlGUI.main(new String[] {});
        } else if ("partnerservice".equalsIgnoreCase(args[0])) {
            PartnerServiceControlGUI.main(new String[] {});
        } else if ("tests-per-group".equalsIgnoreCase(args[0])) {
            TestsPerGroup.main(new String[] {});
            DockerMain.main(createArgsWithoutFirstValue(args));
        } else if ("loader".equalsIgnoreCase(args[0])) {
        } else if ("create-initial-database".equalsIgnoreCase(args[0])) {
            CreateInitialDatabase.main(createArgsWithoutFirstValue(args));
        } else if ("calibrate".equalsIgnoreCase(args[0])) {
            TimeoutCalibrator.main(createArgsWithoutFirstValue(args));
        }else if ("docker".equalsIgnoreCase(args[0])) {
        } else {
            printUsage();
        }
    }

    private static String[] createArgsWithoutFirstValue(String... args) {
        String[] bpelArgs = new String[args.length - 1];
        System.arraycopy(args, 1, bpelArgs, 0, bpelArgs.length);
        return bpelArgs;
    }

    private static void printUsage() {
        System.out.println("The first argument must be bpel, bpmn, engine, enginecontrol, partnerservice, analytics, tests-per-group, process, calibrate or json");
        System.out.println("");
        System.out.println("\tbpel\t\t\tRun benchmarks for BPEL engines");
        System.out.println("\tbpmn\t\t\tRun benchmarks for BPMN engines");
        System.out.println("");
        System.out.println("\tengine\t\t\tList and control supported engines");
        System.out.println("\tprocess\t\t\tList supported benchmark processes");
        System.out.println("\ttests-per-group\t\tList processes per group");
        System.out.println("");
        System.out.println("\tcalibrate\t\tCalibrate timeouts");
        System.out.println("");
        System.out.println("\tenginecontrol\t\tStart GUI to list and control supported engines");
        System.out.println("\tpartnerservice\t\tStart GUI to list and control partner services");
        System.out.println("");
        System.out.println("\tanalytics\t\tCreate dashboard using the results.csv file");
        System.out.println("\tjson\t\t\tCreate json files containing test, engine and feature data");
        System.out.println("");
        System.out.println("\tloader\t\t\tLoads run(s) into database");
        System.out.println("\tcreate-initial-database\t\t\tCreates initial database");
    }
}

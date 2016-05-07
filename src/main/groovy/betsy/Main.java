package betsy;

import betsy.bpel.BPELMain;
import betsy.bpmn.BPMNMain;
import betsy.tools.JsonGenerator;

public final class Main {

    private Main() {}

    public static void main(String... args) {
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
        } else if("json".equalsIgnoreCase(args[0])) {
            JsonGenerator.main(new String[]{});
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
        System.out.println("The first argument must be bpel, bpmn, engine, process or json");
    }
}

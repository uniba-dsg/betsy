package betsy.tools;

import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;

public class ProcessMain {

    public static void main(String... args) {
        if(args.length != 2) {
            printUsage();
            return;
        }

        if ("bpel".equalsIgnoreCase(args[0])) {
            bpel(args[1]);
        } else if ("bpmn".equalsIgnoreCase(args[0])) {
            bpmn(args[1]);
        } else {
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("[bpel|bpmn] [list|count]");
    }

    private static void bpel(String arg) {
        BPELProcessRepository repo = BPELProcessRepository.INSTANCE;

        if ("list".equalsIgnoreCase(arg)) {
            repo.getNames().forEach(System.out::println);
        } else if ("count".equalsIgnoreCase(arg)) {
            System.out.println(repo.getNames().size());
        }
    }

    private static void bpmn(String arg) {
        BPMNProcessRepository repo = new BPMNProcessRepository();

        if ("list".equalsIgnoreCase(arg)) {
            repo.getNames().forEach(System.out::println);
        } else if ("count".equalsIgnoreCase(arg)) {
            System.out.println(repo.getNames().size());
        }
    }
}

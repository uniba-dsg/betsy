package betsy;

import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;

public final class ProcessMain {

    private ProcessMain() {}

    public static void main(String... args) {
        if ("bpel".equalsIgnoreCase(args[0])) {
            bpel(args[1]);
        } else if ("bpmn".equalsIgnoreCase(args[0])) {
            bpmn(args[1]);
        } else {
            System.out.println("[bpel|bpmn] [list|count]");
        }
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

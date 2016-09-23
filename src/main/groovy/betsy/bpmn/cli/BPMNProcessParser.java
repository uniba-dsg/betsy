package betsy.bpmn.cli;

import pebl.test.Test;
import configuration.bpmn.BPMNProcessRepository;

import java.util.List;

public class BPMNProcessParser {

    private final String[] args;

    public BPMNProcessParser(String... args) {
        this.args = args;
    }

    public List<Test> parse() {
        BPMNProcessRepository repository = new BPMNProcessRepository();
        if (args.length <= 1) {
            return repository.getByName("ALL");
        } else {
            return repository.getByNames(args[1].split(","));
        }

    }

    public String[] getArgs() {
        return args;
    }

}

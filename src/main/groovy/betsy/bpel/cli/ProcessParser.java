package betsy.bpel.cli;

import pebl.benchmark.test.Test;
import configuration.bpel.BPELProcessRepository;

import java.util.List;

public class ProcessParser {

    private final String[] args;

    public ProcessParser(String... args) {
        this.args = args;
    }

    public List<Test> parse() {
        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        if (args.length <= 1) {
            return repository.getByName("ALL");
        } else {
            return repository.getByNames(args[1].split(","));
        }
    }

}

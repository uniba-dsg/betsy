package betsy.bpel.cli;

import java.util.List;

import configuration.bpel.BPELProcessRepository;
import pebl.benchmark.test.Test;

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

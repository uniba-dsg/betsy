package betsy.bpmn.cli;

import betsy.common.model.input.EngineIndependentProcess;
import configuration.bpmn.BPMNProcessRepository;

import java.util.List;

public class BPMNProcessParser {
    public BPMNProcessParser(String... args) {
        this.args = args;
    }

    public List<EngineIndependentProcess> parse() {
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

    private final String[] args;
}

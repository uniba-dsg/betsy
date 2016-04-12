package betsy.bpel.cli;

import betsy.bpel.model.BPELProcess;
import betsy.common.model.EngineIndependentProcess;
import configuration.bpel.BPELProcessRepository;

import java.util.List;

public class ProcessParser {

    private final String[] args;

    public ProcessParser(String... args) {
        this.args = args;
    }

    public List<EngineIndependentProcess> parse() {
        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        if (args.length <= 1) {
            return repository.getByName("ALL");
        } else {
            return repository.getByNames(args[1].split(","));
        }
    }

}

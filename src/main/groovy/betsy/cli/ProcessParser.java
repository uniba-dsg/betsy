package betsy.cli;

import betsy.data.BetsyProcess;
import configuration.ProcessRepository;

import java.util.List;

public class ProcessParser {

    private final String[] args;

    public ProcessParser(String[] args) {
        this.args = args;
    }

    public List<BetsyProcess> parse() {
        ProcessRepository repository = new ProcessRepository();
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

package betsy.bpel.cli;

import java.util.List;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.repositories.BPELEngineRepository;

public class EngineParser {
    /**
     * Name of the engines, separated by comma (,), case does not matter, duplicates are filtered out
     * <p>
     * Example:
     * ode,BPELG,OpenESB
     */
    private final String[] args;

    public EngineParser(String... args) {
        this.args = args;
    }

    public List<AbstractBPELEngine> parse() {
        BPELEngineRepository BPELEngineRepository = new BPELEngineRepository();

        if (args.length == 0) {
            // local engines are default
            return BPELEngineRepository.getByName("all");
        } else {
            String[] names = args[0].trim().toLowerCase().split(",");
            return BPELEngineRepository.getByNames(names);
        }

    }

}

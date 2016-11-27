package betsy.bpmn.cli;

import java.util.List;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.repositories.BPMNEngineRepository;

public class BPMNEngineParser {

    /**
     * Name of the engines, separated by comma (,), case does not matter, duplicates are filtered out
     * <p>
     * Example:
     * all, camunda
     */
    private final String[] args;

    public BPMNEngineParser(String... args) {
        this.args = args;
    }

    public List<AbstractBPMNEngine> parse() {
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();

        if (args.length == 0) {
            // all engines are default
            return engineRepository.getByName("all");
        } else {
            String[] names = args[0].trim().toLowerCase().split(",");
            return engineRepository.getByNames(names);
        }

    }

    public String[] getArgs() {
        return args;
    }

}

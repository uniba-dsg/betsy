package betsy.cli;

import betsy.data.engines.Engine;
import betsy.repositories.EngineRepository;

import java.util.List;

public class EngineParser {
    /**
     * Name of the engines, separated by comma (,), case does not matter, duplicates are filtered out
     * <p>
     * Example:
     * ode,BPELG,OpenESB
     */
    private final String[] args;

    public EngineParser(String[] args) {
        this.args = args;
    }

    public List<Engine> parse() {
        EngineRepository engineRepository = new EngineRepository();

        if (args.length == 0) {
            // local engines are default
            return engineRepository.getByName("all");
        } else {
            String[] names = args[0].trim().toLowerCase().split(",");
            return engineRepository.getByNames(names);
        }

    }

}

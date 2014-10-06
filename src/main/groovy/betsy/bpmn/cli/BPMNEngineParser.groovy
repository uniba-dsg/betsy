package betsy.bpmn.cli

import betsy.bpmn.engines.BPMNEngine
import betsy.bpmn.repositories.BPMNEngineRepository

class BPMNEngineParser {
    /**
     * Name of the engines, separated by comma (,), case does not matter, duplicates are filtered out
     *
     * Example:
     * all, camunda
     */
    String[] args

    List<BPMNEngine> parse() {
        BPMNEngineRepository engineRepository = new BPMNEngineRepository()

        if (args.length == 0) {
            // all engines are default
            return engineRepository.getByName("all")
        } else {
            String[] names = args[0].trim().toLowerCase().split(",")
            return engineRepository.getByNames(names)
        }
    }
}

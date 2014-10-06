package betsy.bpmn.cli

import betsy.bpmn.model.BPMNProcess
import configuration.bpmn.BPMNProcessRepository

class BPMNProcessParser {
    String[] args

    List<BPMNProcess> parse() {
        BPMNProcessRepository repository = new BPMNProcessRepository()
        if (args.length <= 1) {
            repository.getByName("ALL")
        } else {
            repository.getByNames(args[1].split(","))
        }
    }
}

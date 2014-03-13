package betsy.cli

import betsy.data.BPMNProcess
import configuration.BPMNProcessRepository

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

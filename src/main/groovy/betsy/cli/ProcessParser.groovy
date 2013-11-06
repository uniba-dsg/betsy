package betsy.cli

import betsy.data.BetsyProcess
import configuration.ProcessRepository

class ProcessParser {

    String[] args

    List<BetsyProcess> parse() {
        ProcessRepository repository = new ProcessRepository()
        if (args.length <= 1) {
            repository.getByName("ALL")
        } else {
            repository.getByNames(args[1].split(","))
        }
    }
}

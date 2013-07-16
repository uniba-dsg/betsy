package configuration.cli

import betsy.data.BetsyProcess
import configuration.processes.Processes

class ProcessParser {

    String[] args

    List<BetsyProcess> parse() {
        return parseWithDuplicates().unique()
    }

    private List<BetsyProcess> parseWithDuplicates() {
        if (args.length <= 1) {
            ["ALL"].collect() { new Processes().get(it) }.flatten()
        } else {
            args[1].split(",").collect() { new Processes().get(it) }.flatten()
        }
    }
}

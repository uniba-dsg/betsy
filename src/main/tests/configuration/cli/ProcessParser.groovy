package configuration.cli

import configuration.processes.Processes

class ProcessParser {

    String[] args

    List<betsy.data.Process> parse() {
        return parseWithDuplicates().unique()
    }

    private List<betsy.data.Process> parseWithDuplicates() {
        if (args.length <= 1) {
            ["ALL"].collect() { new Processes().get(it) }.flatten()
        } else {
            args[1].split(",").collect() { new Processes().get(it) }.flatten()
        }
    }
}

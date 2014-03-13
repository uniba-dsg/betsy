package betsy.cli

import betsy.repositories.BPMNEngineRepository
import configuration.BPMNProcessRepository

class BPMNCliParser {
    private CliBuilder cli
    private def options

    public BPMNCliParser(){
        BPMNEngineRepository engineRepository = new BPMNEngineRepository()
        BPMNProcessRepository processRepository = new BPMNProcessRepository()

        cli = new CliBuilder(
                usage: "betsy [options] <engines> <processes>",
                header: "\nOptions:\n",
                footer: "\nGROUPS for <engines> and <processes> are in CAPITAL LETTERS.\n<engines>: ${engineRepository.names.join(", ")}\n\n\n<processes>: ${processRepository.names.join(", ")}"
        )
        cli.o(longOpt: 'open-results-in-browser', "Opens results in default browser")
        cli.h(longOpt: 'help', "Print out usage information")
        cli.b(longOpt: 'build-only', "Builds only the artifacts. Does nothing else.")
    }

    /**
     * Parse the args values.
     *
     * @param args
     */
    public void parse(String[] args) {
        options = cli.parse(args)
        if (options == null || options == false) {
            throw new IllegalArgumentException("Input arguments ${args} were not parseable.")
        }
    }

    public boolean showUsage() {
        options.h
    }

    public String usage() {
        return cli.usage()
    }

    public boolean openResultsInBrowser() {
        options.o
    }

    public boolean onlyBuildSteps() {
        options.b
    }

    /**
     * @return the arguments without any options
     */
    public String[] arguments() {
        options.arguments() as String[]
    }
}

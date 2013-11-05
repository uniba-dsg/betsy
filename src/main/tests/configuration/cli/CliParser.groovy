package configuration.cli

import betsy.Configuration
import configuration.engines.EngineRepository
import configuration.processes.ProcessRepository

/**
 * Parses the command line options and provides a stable interface for handling the options.
 */
class CliParser {

    private CliBuilder cli
    private def options

    public CliParser() {
        cli = new CliBuilder(
                usage: "betsy [options] <engines> <processes>",
                header: "\nOptions:\n",
                footer: "\nGROUPS for <engines> and <processes> are in CAPITAL LETTERS.\n<engines>: ${new EngineRepository().names.join(", ")}\n\n\n<processes>: ${new ProcessRepository().names.join(", ")}"
        )
        cli.o(longOpt: 'open-results-in-browser', "Opens results in default browser")
        cli.h(longOpt: 'help', "Print out usage information")
        cli.p(longOpt: 'partner-address', args: 1, argName: 'ip-and-port', "Partner IP and Port (defaults to ${Configuration.config.partner.ipAndPort})")
        cli.c(longOpt: 'check-deployment', "Verifies deployment instead of test success")
        cli.t(longOpt: 'to-core-bpel', args: 1, argName: 'transformations', "Transform to Core BPEL")
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

    public boolean checkDeployment() {
        options.c
    }

    public boolean hasCustomPartnerAddress() {
        options.p != null && options.p != false
    }

    public boolean transformToCoreBpel() {
        options.t != null && options.t != false
    }

    public String getCoreBPELTransformations() {
        options.t
    }

    public String getCustomPartnerAddress() {
        options.p
    }

    /**
     * @return the arguments without any options
     */
    public String[] arguments() {
        options.arguments() as String[]
    }

}
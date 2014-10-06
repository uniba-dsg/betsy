package betsy.bpel.cli

import betsy.common.config.Configuration;
import betsy.bpel.repositories.EngineRepository
import configuration.bpel.ProcessRepository

/**
 * Parses the command line options and provides a stable interface for handling the options.
 */
class CliParser {

    private CliBuilder cli
    private def options

    public CliParser() {
        EngineRepository engineRepository = new EngineRepository()
        ProcessRepository processRepository = new ProcessRepository()
        cli = new CliBuilder(
                usage: "betsy [options] <engines> <processes>",
                header: "\nOptions:\n",
                footer: "\nGROUPS for <engines> and <processes> are in CAPITAL LETTERS.\n<engines>: ${engineRepository.names.join(", ")}\n\n\n<processes>: ${processRepository.names.join(", ")}"
        )
        cli.o(longOpt: 'open-results-in-browser', "Opens results in default browser")
        cli.h(longOpt: 'help', "Print out usage information")
        cli.p(longOpt: 'partner-address', args: 1, argName: 'ip-and-port', "Partner IP and Port (defaults to ${Configuration.get("partner.ipAndPort")})")
        cli.c(longOpt: 'check-deployment', "Verifies deployment instead of test success")
        cli.t(longOpt: 'to-core-bpel', args: 1, argName: 'transformations', "Transform to Core BPEL")
        cli.e(longOpt: 'use-external-partner-service', "Use external partner service instead of internal one")
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

    public void printUsage() {
        cli.usage()
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

    public boolean useExternalPartnerService() {
        options.e
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

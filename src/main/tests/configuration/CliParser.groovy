package configuration

import betsy.Configuration

/**
 * Parses the command line options and provides a stable interface for handling the options.
 */
class CliParser {

    private CliBuilder cli
    private def options

    public CliParser() {
        cli = new CliBuilder(usage: "[options] <engines> <process>")
        cli.o(longOpt: 'open-results-in-browser', "Opens results in default browser")
        cli.h(longOpt: 'help', "Print out usage information")
        cli.p(longOpt: 'partner-address', args: 1, argName: 'ip-and-port', "Partner IP and Port (defaults to ${Configuration.config.PARTNER_IP_AND_PORT})")
        cli.c(longOpt: 'check-deployment', "Verifies deployment instead of test success")
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

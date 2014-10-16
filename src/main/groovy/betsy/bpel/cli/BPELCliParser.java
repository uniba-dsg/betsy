package betsy.bpel.cli;

import betsy.bpel.engines.Engine;
import betsy.bpel.model.BetsyProcess;
import betsy.bpel.repositories.EngineRepository;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.config.Configuration;
import configuration.bpel.ProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.apache.commons.cli.*;

import java.util.Collections;
import java.util.List;

public class BPELCliParser {

    public static final BPELCliParameter HELP_ONLY = new BPELCliParameter() {
        @Override
        public List<Engine> getEngines() {
            return Collections.emptyList();
        }

        @Override
        public List<BetsyProcess> getProcesses() {
            return Collections.emptyList();
        }

        @Override
        public boolean openResultsInBrowser() {
            return false;
        }

        @Override
        public boolean checkDeployment() {
            return false;
        }

        @Override
        public boolean hasCustomPartnerAddress() {
            return false;
        }

        @Override
        public boolean transformToCoreBpel() {
            return false;
        }

        @Override
        public String getCoreBPELTransformations() {
            return null;
        }

        @Override
        public String getCustomPartnerAddress() {
            return null;
        }

        @Override
        public boolean useExternalPartnerService() {
            return false;
        }

        @Override
        public boolean buildArtifactsOnly() {
            return false;
        }


        @Override
        public boolean showHelp() {
            return true;
        }
    };
    public static final String HELP = "help";
    public static final String OPEN_RESULTS_IN_BROWSER = "open-results-in-browser";
    public static final String CHECK_DEPLOYMENT = "check-deployment";
    public static final String USE_EXTERNAL_PARTNER_SERVICE = "use-external-partner-service";
    public static final String BUILD_ONLY = "build-only";
    public static final String PARTNER_ADDRESS = "partner-address";
    public static final String TO_CORE_BPEL = "to-core-bpel";

    private final String[] args;

    public BPELCliParser(String[] args) {
        this.args = args;
    }

    public BPELCliParameter parse() {
        CommandLineParser parser = new BasicParser();
        try {
            CommandLine cmd = parser.parse(getOptions(), args);

            if (cmd.hasOption(HELP)) {
                printUsage();
                System.exit(-1);
            }

            return new BPELCliParameter() {
                @Override
                public List<Engine> getEngines() {
                    return new EngineParser(cmd.getArgs()).parse();
                }

                @Override
                public List<BetsyProcess> getProcesses() {
                    return new ProcessParser(cmd.getArgs()).parse();
                }

                @Override
                public boolean openResultsInBrowser() {
                    return cmd.hasOption(OPEN_RESULTS_IN_BROWSER);
                }

                @Override
                public boolean checkDeployment() {
                    return cmd.hasOption(CHECK_DEPLOYMENT);
                }

                @Override
                public boolean hasCustomPartnerAddress() {
                    return cmd.hasOption(PARTNER_ADDRESS);
                }

                @Override
                public boolean transformToCoreBpel() {
                    return cmd.hasOption(TO_CORE_BPEL);
                }

                @Override
                public String getCoreBPELTransformations() {
                    return cmd.getOptionValue(TO_CORE_BPEL);
                }

                @Override
                public String getCustomPartnerAddress() {
                    return cmd.getOptionValue(PARTNER_ADDRESS);
                }

                @Override
                public boolean useExternalPartnerService() {
                    return cmd.hasOption(USE_EXTERNAL_PARTNER_SERVICE);
                }

                @Override
                public boolean buildArtifactsOnly() {
                    return cmd.hasOption(BUILD_ONLY);
                }

                @Override
                public boolean showHelp() {
                    return cmd.hasOption(HELP);
                }
            };
        } catch (ParseException e) {
            return HELP_ONLY;
        }
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption("o", OPEN_RESULTS_IN_BROWSER, false, "Opens results in default browser");
        options.addOption("h", HELP, false, "Print usage information.");
        options.addOption("c", CHECK_DEPLOYMENT, false, "Verifies deployment instead of test success");
        options.addOption("e", USE_EXTERNAL_PARTNER_SERVICE, false, "Use external partner service instead of internal one");
        options.addOption("b", BUILD_ONLY, false, "Builds only the artifacts. Does nothing else.");

        options.addOption("p", PARTNER_ADDRESS, true, "Partner IP and Port (defaults to " + Configuration.get("partner.ipAndPort") + ")");
        options.addOption("t", TO_CORE_BPEL, true, "Transform to Core BPEL");
        return options;
    }

    public void printUsage() {
        String firstLine = "betsy bpel [OPTIONS] <ENGINES> <PROCESSES>";
        String header = "\nOptions:\n";
        String footer = "\nGROUPS for <ENGINES> and <PROCESSES> are in CAPITAL LETTERS.\n" +
                "<ENGINES>: " + new EngineRepository().getNames() + "\n\n\n" +
                "<PROCESSES>: " + new ProcessRepository().getNames() + "\n\n\n" +
                "Please report issues at https://github.com/uniba-dsg/betsy/issues";
        new HelpFormatter().printHelp(firstLine,
                header,
                getOptions(),
                footer);
    }

}



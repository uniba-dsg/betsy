package betsy.bpmn.cli;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.repositories.BPMNEngineRepository;
import configuration.bpmn.BPMNProcessRepository;
import org.apache.commons.cli.*;

import java.util.Collections;
import java.util.List;

public class BPMNCliParser {

    public static final BPMNCliParameter HELP_ONLY = new BPMNCliParameter() {
        @Override
        public List<AbstractBPMNEngine> getEngines() {
            return Collections.emptyList();
        }

        @Override
        public List<BPMNProcess> getProcesses() {
            return Collections.emptyList();
        }

        @Override
        public boolean openResultsInBrowser() {
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
    public static final String BUILD_ONLY = "build-only";
    public static final String OPEN_RESULTS_IN_BROWSER = "open-results-in-browser";

    private final String[] args;

    public BPMNCliParser(String[] args) {
        this.args = args;
    }

    public BPMNCliParameter parse() {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(getOptions(), args);

            for(Option option : cmd.getOptions()) {
                System.out.println(option.toString());
            }

            if (cmd.hasOption(HELP)) {
                printUsage();
                System.exit(-1);
            }

            return new BPMNCliParameter() {
                @Override
                public List<AbstractBPMNEngine> getEngines() {
                    return new BPMNEngineParser(cmd.getArgs()).parse();
                }

                @Override
                public List<BPMNProcess> getProcesses() {
                    return new BPMNProcessParser(cmd.getArgs()).parse();
                }

                @Override
                public boolean openResultsInBrowser() {
                    return cmd.hasOption(OPEN_RESULTS_IN_BROWSER);
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
        options.addOption("b", BUILD_ONLY, false, "Builds only the artifacts. Does nothing else.");
        options.addOption("h", HELP, false, "Print usage information.");
        return options;
    }

    public void printUsage() {
        String firstLine = "betsy bpmn [OPTIONS] <ENGINES> <PROCESSES>";
        String header = "\nOptions:\n";
        String footer = "\nGROUPS for <ENGINES> and <PROCESSES> are in CAPITAL LETTERS.\n" +
                "<ENGINES>: " + new BPMNEngineRepository().getNames() + "\n\n\n" +
                "<PROCESSES>: " + new BPMNProcessRepository().getNames() + "\n\n\n" +
                "Please report issues at https://github.com/uniba-dsg/betsy/issues";
        new HelpFormatter().printHelp(firstLine,
                header,
                getOptions(),
                footer);
    }

}

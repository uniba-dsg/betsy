package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DataLogChecker {

    private static final Logger LOGGER = Logger.getLogger(DataLogChecker.class);

    private final Path logFile;

    private final Path dataLogFile;

    private final List<String> values = Arrays.asList("String");

    public DataLogChecker(Path logFile, Path dataLogFile) {
        FileTasks.assertFile(logFile);
        FileTasks.assertFile(dataLogFile);

        this.logFile = logFile;
        this.dataLogFile = dataLogFile;
    }

    public void checkDataTypes() {
        try {
            List<String> lines = readLogFile();
            values.forEach((value) -> {
                if (contains(lines, value)) {
                    BPMNAssertions.appendToFile(logFile, BPMNAssertions.DATA_CORRECT);
                }
            });
        } catch (IllegalArgumentException e) {
            LOGGER.info("Could not validate parallel execution", e);
        }
    }

    private List<String> readLogFile() {
        List<String> lines = new LinkedList<>();
        try {
            lines = Files.readAllLines(dataLogFile, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            LOGGER.info("The content of the file for type check could not be read", e);
        }
        return lines;
    }

    private boolean contains(List<String> list, String string) {
        for (String entry : list) {
            if (entry.equals(string)) return true;
        }
        return false;
    }
}

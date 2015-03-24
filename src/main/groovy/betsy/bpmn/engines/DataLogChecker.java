package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DataLogChecker {

    private static final Logger LOGGER = Logger.getLogger(DataLogChecker.class);

    private Path logFile;
    private Path dataLogFile;

    private List<String> values = Arrays.asList("String", String.valueOf(Long.MAX_VALUE));

    public DataLogChecker(Path logFile, Path dataLogFile) {
        FileTasks.assertFile(logFile);
        FileTasks.assertFile(dataLogFile);

        this.logFile = logFile;
        this.dataLogFile = dataLogFile;
    }

    public void checkDataTypes(BPMNTestCase tc) {
        try {
            List<String> lines = readLogFile();
            values.forEach((value) -> {
                if (contains(lines, value)) {
                    writeLogFile(true);
                }
            });
        } catch (IllegalArgumentException e) {
            writeLogFile(false);
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

    private void writeLogFile(boolean wasSuccessful) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile.toString(), true))) {
            if (wasSuccessful) {
                bw.append(BPMNAssertions.DATA_CORRECT.toString());
            } else {
                bw.append("");
            }
            bw.newLine();
        } catch (IOException e) {
            LOGGER.info("Writing result to file failed", e);
        }
    }

}

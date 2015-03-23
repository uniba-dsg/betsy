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
import java.util.LinkedList;
import java.util.List;

public class DataLogChecker {

    private static final Logger LOGGER = Logger.getLogger(DataLogChecker.class);

    public static void checkDataTypes(BPMNTestCase tc, final Path logFile) {
        // Only check when asked for a data type assertion
        if (!tc.getAssertions().contains(BPMNAssertions.DATA_CORRECT.toString())) {
            return;
        }

        List<String> lines = readLogFile(logFile);
        if (contains(lines, "String")) {
            writeLogFile(logFile);
        }
        if (contains(lines, String.valueOf(Long.MAX_VALUE))) {
            writeLogFile(logFile);
        }
    }

    private static List<String> readLogFile(final Path logFile) {
        FileTasks.assertFile(logFile);
        List<String> lines = new LinkedList<>();
        Path dataLogFile = Paths.get(logFile.toString().replaceAll("\\.txt", "_data.txt"));
        try {
            lines = Files.readAllLines(dataLogFile, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            LOGGER.info("The content of the file for type check could not be read", e);
        }
        return lines;
    }

    private static boolean contains(List<String> list, String string) {
        for (String entry : list) {
            if (entry.equals(string)) return true;
        }
        return false;
    }

    private static void writeLogFile(final Path logFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile.toString(), true))) {
            bw.append(BPMNAssertions.DATA_CORRECT.toString());
            bw.newLine();
        } catch (IOException e) {
            LOGGER.info("Writing result to file failed", e);
        }
    }



}

package betsy.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Csv file for the duration of the tasks. Number of elements may vary per line. This is easier to analyze in excel.
 * <p>
 * Structure: DURATION IN MS;NAME;NAME.split("/").join(";")
 * Example: 10;test;test
 * Example: 10;first/second/third;first;second;third
 */
public class DurationCsv {

    public static final String CSV_SEPARATOR = ";";

    private final Path storage;

    public DurationCsv(Path storage) {
        this.storage = Objects.requireNonNull(storage);
    }

    public void saveTaskDuration(String taskName, long durationInMilliseconds) {
        try (BufferedWriter writer = Files.newBufferedWriter(storage, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
            writer.append(String.valueOf(durationInMilliseconds));
            writer.append(CSV_SEPARATOR);
            writer.append(taskName);
            writer.append(taskName.replaceAll("\\\\", "/").replaceAll("/", CSV_SEPARATOR));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("could not save task duration " + durationInMilliseconds + " for task " +
                    taskName + " in file " + storage, e);
        }
    }

    public static Map<String, Long> readDurations(Path durationsCsv) {
        Map<String, Long> idToDuration = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(durationsCsv, StandardCharsets.UTF_8)) {
            String line = br.readLine();
            while (line != null) {
                String[] tokens = line.split(CSV_SEPARATOR);
                if (tokens.length == 4) {
                    long duration = Long.parseLong(tokens[0]);
                    String engineTestId = tokens[2] + "__" + tokens[3];
                    idToDuration.put(engineTestId, duration);
                }
                line = br.readLine();
            }
        } catch (IOException ioe) {
        }
        return idToDuration;
    }

}

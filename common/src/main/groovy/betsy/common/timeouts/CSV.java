package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class CSV {

    private static final Logger LOGGER = Logger.getLogger(CSV.class);

    /**
     * This method deletes the csv file and writes the values of timeouts to a csv file.
     *
     * @param csv      The csv path, where the timeout values should be saved.
     * @param timeouts The timeouts to save.
     */
    public static void write(Path csv, List<CalibrationTimeout> timeouts) {
        FileTasks.deleteFile(Objects.requireNonNull(csv, "The csv file can't be null."));
        write(csv, Objects.requireNonNull(timeouts, "The timeouts can't be null."), 1);
    }

    /**
     * This method extends the csv file with the values of timeouts.
     *
     * @param csv               The csv path, where the timeout values should be saved.
     * @param timeouts          The timeouts to save.
     * @param numberOfIteration The number of calibration iterations.
     */
    public static void write(Path csv, List<CalibrationTimeout> timeouts, int numberOfIteration) {
        Objects.requireNonNull(csv, "The csv file can't be null.");
        try (PrintWriter writer = new PrintWriter(new FileWriter(csv.toFile(), true))) {
            if (!csv.toFile().exists()) {
                writer.append("Iteration").append(';');
                writer.append("Key").append(';');
                writer.append("TimeStamp").append(';');
                writer.append("Status").append(';');
                writer.append("EngineOrProcessGroup").append(';');
                writer.append("StepOrProcess").append(';');
                writer.append("Description").append(';');
                writer.append("Measured time").append(';');
                writer.append("Value").append(';');
                writer.append("TimeToRepetition").append('\n');
            } else {
                for (CalibrationTimeout timeout : timeouts) {
                    writer.append(Integer.toString(numberOfIteration)).append(';');
                    writer.append(timeout.getKey()).append(';');
                    writer.append(Long.toString(timeout.getTimestamp())).append(';');
                    writer.append(timeout.getStatus().toString()).append(';');
                    writer.append(timeout.getEngineOrProcessGroup()).append(';');
                    writer.append(timeout.getStepOrProcess()).append(';');
                    writer.append(timeout.getDescription()).append(';');
                    writer.append(Integer.toString(timeout.getMeasuredTime())).append(';');
                    writer.append(Integer.toString(timeout.getTimeoutInMs())).append(';');
                    writer.append(Integer.toString(timeout.getTimeToRepetitionInMs())).append('\n');
                }
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            LOGGER.info("Could not write the timeouts to csv file.");
        }
    }

    /**
     * This method reads the csv file and returns a list with CalibrationTimeouts.
     *
     * @param csv The csv, which should be read.
     * @return The list with the CalibrationTimeouts.
     */
    public static List<CalibrationTimeout> read(Path csv) {
        Objects.requireNonNull(csv, "The csv file can't be null.");
        List<CalibrationTimeout> timeouts = new ArrayList<>();
        try(BufferedReader reader = Files.newBufferedReader(csv)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] entries = line.split(";");
                CalibrationTimeout timeout = new CalibrationTimeout(entries[4], entries[5], entries[6], Integer.valueOf(entries[8]), Integer.valueOf(entries[9]), Integer.valueOf(entries[7]));
                timeouts.add(timeout);
            }
        } catch (IOException e) {
            LOGGER.info("Could not read the timeouts from csv file.");
        }
        return timeouts;
    }

}

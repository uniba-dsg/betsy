package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataLogChecker {

    private static final Logger LOGGER = Logger.getLogger(DataLogChecker.class);

    private final Path logFile;

    private final Path dataLogFile;

    private final String stringToFind;

    public DataLogChecker(Path logFile, Path dataLogFile, String stringToFind) {
        FileTasks.assertFile(logFile);
        FileTasks.assertFile(dataLogFile);

        this.logFile = logFile;
        this.dataLogFile = dataLogFile;
        this.stringToFind = stringToFind;
    }

    public void checkDataTypes() {
        try {
            List<String> linesInDataLog = Files.readAllLines(dataLogFile, Charset.defaultCharset());
            if(linesInDataLog.contains(stringToFind)) {
                BPMNAssertions.appendToFile(logFile, BPMNAssertions.DATA_CORRECT);
            }
        } catch (IOException e) {
            LOGGER.info("Cannot parse content of "+dataLogFile.toString());
        }
    }
}

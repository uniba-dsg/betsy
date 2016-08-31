package betsy.bpmn.engines;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

public class OverlappingTimestampChecker {

    private static final Logger LOGGER = Logger.getLogger(OverlappingTimestampChecker.class);

    private final Path logFile;

    private final Path logParallelOne;

    private final Path logParallelTwo;

    public OverlappingTimestampChecker(Path logFile, Path logParallelOne, Path logParallelTwo) {
        FileTasks.assertFile(logFile);
        FileTasks.assertFile(logParallelOne);
        FileTasks.assertFile(logParallelTwo);

        this.logFile = logFile;
        this.logParallelOne = logParallelOne;
        this.logParallelTwo = logParallelTwo;
    }

    public void checkParallelism() {
        // Read all Lines from files
        List<String> listOne = new ArrayList<>();
        List<String> listTwo = new ArrayList<>();
        try {
            listOne = Files.readAllLines(logParallelOne, StandardCharsets.ISO_8859_1);
            listTwo = Files.readAllLines(logParallelTwo, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            LOGGER.info("The content of a file for validation of parallel execution could not be read", e);
        }

        // Parse Longs
        if (listOne.size() == 2 && listTwo.size() == 2) {
            long startOne;
            long startTwo;
            long endOne;
            long endTwo;

            try {
                startOne = Long.parseLong(listOne.get(0));
                endOne = Long.parseLong(listOne.get(1));
                startTwo = Long.parseLong(listTwo.get(0));
                endTwo = Long.parseLong(listTwo.get(1));
            } catch (NumberFormatException e) {
                LOGGER.info("Content of a file was not parsable to string ", e);
                return;
            }

            // Compare intervals
            boolean wasParallel = false;
            if (startOne <= startTwo && startTwo < endOne) {
                wasParallel = true;
            } else if (startTwo <= startOne && startOne < endTwo) {
                wasParallel = true;
            }

            // Write result of comparison to file
            if(wasParallel) {
                BPMNAssertions.appendToFile(logFile, BPMNAssertions.EXECUTION_PARALLEL);
            }

        } else {
            LOGGER.info("Files do not contain expected line count");
        }

    }

}

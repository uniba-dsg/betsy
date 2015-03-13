package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.bpmn.model.BPMNTestCase;
import betsy.common.engines.tomcat.Tomcat;
import betsy.common.tasks.FileTasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OverlappingTimestampChecker {

    private final Path logFile;
    private final Path logParallelOne;
    private final Path logParallelTwo;

    public OverlappingTimestampChecker(Path logFile, Path logParallelOne, Path logParallelTwo) {
        FileTasks.assertFile(logFile);

        this.logFile = logFile;
        this.logParallelOne = logParallelOne;
        this.logParallelTwo = logParallelTwo;
    }

    public void checkParallelism() {
        // Get paths from needed files

        // Read all Lines from files
        List<String> listOne = new ArrayList();
        List<String> listTwo = new ArrayList();
        try {
            listOne = Files.readAllLines(logParallelOne, StandardCharsets.ISO_8859_1);
            listTwo = Files.readAllLines(logParallelTwo, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
           // ignored
        }

        // Parse Longs
        long start1 = 0, start2 = 0, end1 = 0, end2 = 0;
        start1 = Long.parseLong(listOne.get(0));
        end1 = Long.parseLong(listOne.get(1));
        start2 = Long.parseLong(listTwo.get(0));
        end2 = Long.parseLong(listTwo.get(1));

        // Compare intervals
        boolean wasParallel = false;
        if (start1 <= start2 && start2 < end1) {
            wasParallel = true;
        } else if (start2 <= start1 && start1 < end2) {
            wasParallel = true;
        }

        // Write result of comparison to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile.toString(), true))) {
            if (wasParallel) {
                bw.append(BPMNAssertions.EXECUTION_PARALLEL.toString());
            }
            bw.newLine();
        } catch (IOException e) {
        }

    }

}

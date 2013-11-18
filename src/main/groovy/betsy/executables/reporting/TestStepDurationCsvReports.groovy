package betsy.executables.reporting;

import betsy.data.TestSuite
import betsy.data.engines.Engine

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path;

public class TestStepDurationCsvReports {

    /**
     * Represents one row in the resulting csv file
     */
    static class ProcessCsvDuration {
        Path csvFile
        String engine
        String name

        String toRow() {
            //load and split the file
            List<String> lines = Files.readAllLines(csvFile, StandardCharsets.UTF_8)
            String durationLine = lines.get(1)

            return [engine, name, durationLine].join(";")
        }
    }

    /**
     * path to resulting csv file (WRITE)
     */
    Path csv

    TestSuite tests

    public void create() {
        // TODO code to create durations.csv per process is lost somehow
        return

        csv.toFile().withPrintWriter { w ->
            w.println("Engine;Process;Total;Build;Installation;Startup;Deploy;Test;Shutdown")
        }

        tests.engines.each { Engine engine ->
            engine.processes.each { process ->
                Path processDurationFile = process.targetPath.resolve("durations.csv")
                csv.toFile().withWriterAppend { w ->
                    ProcessCsvDuration csvRow = new ProcessCsvDuration(
                            csvFile: processDurationFile,
                            name: process.normalizedId,
                            engine: process.engine.toString())
                    w.println(csvRow.toRow())
                }
            }
        }

    }

}

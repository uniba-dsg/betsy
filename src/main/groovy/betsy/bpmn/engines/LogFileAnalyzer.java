package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertions;
import betsy.common.tasks.FileTasks;
import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LogFileAnalyzer {

    private final Path logFile;

    private final Map<String, BPMNAssertions> substrings2error = new HashMap<>();

    public LogFileAnalyzer(Path logFile) {
        FileTasks.assertFile(logFile);

        this.logFile = logFile;
    }

    public void addSubstring(String substring, BPMNAssertions error) {
        substrings2error.put(Objects.requireNonNull(substring, "substring must not be null"),
                Objects.requireNonNull(error, "error must not be null"));
    }

    public Set<BPMNAssertions> getErrors() {
        Set<BPMNAssertions> result = new HashSet<>();

        List<String> lines = getLines();
        for (String line : lines) {
            for (Map.Entry<String, BPMNAssertions> entry : substrings2error.entrySet()) {
                if (line.contains(entry.getKey())) {
                    result.add(entry.getValue());
                }
            }
        }

        return result;
    }

    private List<String> getLines() {
        try {
            return Files.readAllLines(logFile, Charsets.ISO_8859_1);
        } catch (IOException e) {
            try {
                return Files.readAllLines(logFile, Charsets.UTF_8);
            } catch (IOException e1) {
                throw new RuntimeException("could not read file with either ISO 8859 1 or UTF 8" + logFile, e1);
            }
        }
    }
}

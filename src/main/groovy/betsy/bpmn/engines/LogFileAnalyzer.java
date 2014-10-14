package betsy.bpmn.engines;

import betsy.common.tasks.FileTasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LogFileAnalyzer {

    private final Path logFile;

    private final Map<String,Optional<String>> substrings2error = new HashMap<>();

    public LogFileAnalyzer(Path logFile) {
        FileTasks.assertFile(logFile);

        this.logFile = logFile;
    }

    public void addSubstring(String substring, String error){
        substrings2error.put(substring, Optional.of(Objects.requireNonNull(error, "error must not be null")));
    }

    public void addSubstring(String substring){
        substrings2error.put(substring, Optional.empty());
    }

    public Set<String> getErrors() {
        Set<String> result = new HashSet<>();

        List<String> lines = getLines();
        for (String line : lines) {
            for(Map.Entry<String, Optional<String>> entry : substrings2error.entrySet()) {
                if(line.contains(entry.getKey())){
                    result.add(entry.getValue().orElse(line));
                }
            }
        }

        return result;
    }

    private List<String> getLines() {
        try {
            return Files.readAllLines(logFile);
        } catch (IOException e) {
            throw new RuntimeException("could not read file " + logFile, e);
        }
    }
}

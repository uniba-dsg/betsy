package betsy.bpmn.engines;

import betsy.bpmn.model.BPMNAssertion;
import betsy.common.tasks.FileTasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class LogFileAnalyzer {

    private final Path logFile;

    private final Map<String,BPMNAssertion> substrings2error = new HashMap<>();

    public LogFileAnalyzer(Path logFile) {
        FileTasks.assertFile(logFile);

        this.logFile = logFile;
    }

    public void addSubstring(String substring, BPMNAssertion error){
        substrings2error.put(Objects.requireNonNull(substring, "substring must not be null"),
                Objects.requireNonNull(error, "error must not be null"));
    }

    public Set<BPMNAssertion> getErrors() {
        Set<BPMNAssertion> result = new HashSet<>();

        List<String> lines = getLines();
        for (String line : lines) {
            for(Map.Entry<String, BPMNAssertion> entry : substrings2error.entrySet()) {
                if(line.contains(entry.getKey())){
                    result.add(entry.getValue());
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

package betsy.bpmn.engines.camunda;

import betsy.common.tasks.FileTasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CamundaLogFileAnalyzer {

    private final Path logFile;

    public CamundaLogFileAnalyzer(Path logFile) {
        FileTasks.assertFile(logFile);

        this.logFile = logFile;
    }

    public Set<String> getRuntimeErrors() {
        Set<String> result = new HashSet<>();

        List<String> lines = getLines();
        for (String line : lines) {
            if (line.startsWith("org.camunda.bpm.engine.ProcessEngineException")) {
                // TODO will never happen
                result.add("runtimeException");
            }
            //special case for error end event
            if (line.contains("EndEvent_2 throws error event with errorCode 'ERR-1'")) {
                result.add("thrownErrorEvent");
            }
        }

        return result;
    }

    public Set<String> getDeploymentErrors() {
        //look up log for deployment error caused by unsupported activity type
        Set<String> result = new HashSet<>();

        List<String> lines = getLines();
        for (String line : lines) {
            if (line.contains("Ignoring unsupported activity type")) {
                result.add(line);
            }
            if (line.startsWith("org.camunda.bpm.engine.ProcessEngineException")) {
                result.add(line);
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

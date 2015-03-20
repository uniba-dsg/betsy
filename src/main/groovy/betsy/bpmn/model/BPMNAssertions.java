package betsy.bpmn.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BPMNAssertions {



    SCRIPT_task1("SCRIPT_task1"),
    SCRIPT_task2("SCRIPT_task2"),
    SCRIPT_task3("SCRIPT_task3"),
    SCRIPT_task4("SCRIPT_task4"),
    SCRIPT_task5("SCRIPT_task5"),

    ERROR_RUNTIME("ERROR_runtime"),
    ERROR_DEPLOYMENT("ERROR_deployment"),
    ERROR_PROCESS_ABORTED("ERROR_processAborted"),
    ERROR_THROWN_ERROR_EVENT("ERROR_thrownErrorEvent"),
    ERROR_THROWN_ESCALATION_EVENT("ERROR_thrownEscalationEvent"),

    ERROR_GENERIC("ERROR_generic"),

    EXECUTION_PARALLEL("EXECUTION_parallel");

    private final String name;

    private BPMNAssertions(String name) {
        this.name = name;
    }

    public static List<String> getScriptAssertions() {
        return Stream.of(values()).map(Object::toString).filter((x) -> x.startsWith("SCRIPT")).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name;
    }

    public static void appendToFile(Path fileName, BPMNAssertions s) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fileName.toFile(), true))) {
            bw.append(s.toString());
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Could not append assertion " + s + " to " + fileName, e);
        }
    }
}

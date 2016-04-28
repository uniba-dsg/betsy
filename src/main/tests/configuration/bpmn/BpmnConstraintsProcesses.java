package configuration.bpmn;

import betsy.bpmn.model.BPMNTestCase;
import betsy.common.model.feature.Construct;
import betsy.common.model.feature.Feature;
import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.tasks.FileTasks;
import betsy.common.util.FileTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class BpmnConstraintsProcesses {

    static List<EngineIndependentProcess> BPMN_CONSTRAINTS = getBpmnConstraintProcesses();

    private static List<EngineIndependentProcess> getBpmnConstraintProcesses() {
        Path path = Paths.get("src/main/tests/files/bpmn/bpmn_constraints");
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<EngineIndependentProcess> result = new LinkedList<>();

        try {
            Files.walk(path, Integer.MAX_VALUE).forEach(dir -> {

                boolean isTestDirectory = hasFolderBpmnFiles(dir);
                if (isTestDirectory) {
                    List<Path> processes = getBpmnFilesInFolder(dir);
                    processes.stream().forEach(process -> {
                        String constraint = getConstraint(process);
                        result.add(new EngineIndependentProcess(process, FileTasks.getFilenameWithoutExtension(process),
                                Collections.singletonList(new BPMNTestCase().assertDeploymentFailed()),
                                new Feature(new Construct(Groups.BPMN_CONSTRAINTS, constraint),
                                        FileTasks.getFilenameWithoutExtension(process))));
                    });
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException("Could not walk folder " + path, e);
        }

        return result;
    }

    private static boolean hasFolderBpmnFiles(Path dir) {
        try {
            return Files.list(dir).anyMatch(FileTypes::isBpmnFile);
        } catch (IOException e) {
            // no BPMN files if the folder does not exist
            return false;
        }
    }

    private static List<Path> getBpmnFilesInFolder(Path dir) {
        try {
            return Files.list(dir).filter(FileTypes::isBpmnFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("could not find a BPMN file in folder " + dir);
        }
    }

    private static String getConstraint(Path path) {
        return path.getFileName().toString().split("_")[0];
    }
}

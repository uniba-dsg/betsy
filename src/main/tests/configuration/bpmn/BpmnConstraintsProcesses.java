package configuration.bpmn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import betsy.bpmn.model.BPMNTestCaseBuilder;
import betsy.common.tasks.FileTasks;
import betsy.common.util.FileTypes;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.test.Test;

class BpmnConstraintsProcesses {

    static List<Test> BPMN_CONSTRAINTS = getBpmnConstraintProcesses();

    private static List<Test> getBpmnConstraintProcesses() {
        Path path = Paths.get("src/main/tests/files/bpmn/bpmn_constraints");
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<Test> result = new LinkedList<>();

        try {
            Files.walk(path, Integer.MAX_VALUE).forEach(dir -> {

                boolean isTestDirectory = hasFolderBpmnFiles(dir);
                if (isTestDirectory) {
                    List<Path> processes = getBpmnFilesInFolder(dir);
                    processes.forEach(process -> {
                        String constraint = getConstraint(process);
                        result.add(new Test(process, FileTasks.getFilenameWithoutExtension(process),
                                Collections.singletonList(new BPMNTestCaseBuilder().assertDeploymentFailed().getTestCase(1, FileTasks.getFilenameWithoutExtension(process))),
                                new Feature(Groups.BPMN_CONSTRAINTS.getOrCreate(constraint),
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
        try (Stream<Path> paths = Files.list(dir)){
            return paths.anyMatch(FileTypes::isBpmnFile);
        } catch (IOException e) {
            // no BPMN files if the folder does not exist
            return false;
        }
    }

    private static List<Path> getBpmnFilesInFolder(Path dir) {
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.filter(FileTypes::isBpmnFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("could not find a BPMN file in folder " + dir);
        }
    }

    private static String getConstraint(Path path) {
        return path.getFileName().toString().split("_")[0];
    }
}

package configuration.bpel;

import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestCase;
import betsy.common.util.FileTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StaticAnalysisProcesses {

    public static List<BPELProcess> getStaticAnalysisProcesses() {
        Path path = Paths.get("src/main/tests/files/bpel/sa-rules");
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<BPELProcess> result = new LinkedList<>();

        try {
            Files.walk(path, Integer.MAX_VALUE).forEach(dir -> {

                boolean isTestDirectory = hasFolderBpelFiles(dir);
                if (isTestDirectory) {
                    BPELProcess process = new BPELProcess();
                    process.setProcess(getBpelFileInFolder(dir));
                    process.setTestCases(Collections.singletonList(new BPELTestCase().checkFailedDeployment()));

                    process.setWsdls(createWSDLPaths(dir));
                    process.setAdditionalFiles(createXSDPaths(dir));

                    result.add(process);
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException("Could not walk folder " + path, e);
        }

        return result;
    }

    private static Path getBpelFileInFolder(Path dir) {
        try {
            return Files.list(dir).filter(FileTypes::isBpelFile).findFirst().get();
        } catch (IOException e) {
            throw new IllegalStateException("could not find a bpel file in folder " + dir);
        }
    }

    private static boolean hasFolderBpelFiles(Path dir) {
        try {
            return Files.list(dir).anyMatch(FileTypes::isBpelFile);
        } catch (IOException e) {
            // no BPEL files if the folder does not exist
            return false;
        }
    }

    public static Map<String, List<BPELProcess>> getGroupsPerRuleForSAProcesses(List<BPELProcess> processes) {
        Map<String, List<BPELProcess>> result = new HashMap<>();

        IntStream.rangeClosed(1, 95).forEach((n) -> {
            String rule = convertIntegerToSARuleNumber(n);
            List<BPELProcess> processList = processes.stream().filter((p) -> p.getName().startsWith(rule)).collect(Collectors.toList());

            if (!processList.isEmpty()) {
                result.put(rule, processList);
            }
        });

        return result;
    }

    public static String convertIntegerToSARuleNumber(int number) {
        return String.format("SA%05d", number);
    }

    private static List<Path> createXSDPaths(Path dir) {
        try {
            return Files.list(dir).filter(FileTypes::isXsdFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Could not open folder " + dir, e);
        }
    }

    private static List<Path> createWSDLPaths(Path dir) {
        try {
            return Files.list(dir).filter(FileTypes::isWsdlFile).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Could not open folder " + dir, e);
        }
    }

    public static List<BPELProcess> STATIC_ANALYSIS = getStaticAnalysisProcesses();
}

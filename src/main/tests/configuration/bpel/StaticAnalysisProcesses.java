package configuration.bpel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import betsy.bpel.model.BPELTestCase;
import configuration.Capabilities;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.test.Test;
import betsy.common.tasks.FileTasks;
import betsy.common.util.FileTypes;

class StaticAnalysisProcesses {

    static List<Test> STATIC_ANALYSIS = getStaticAnalysisProcesses();

    static List<Test> getStaticAnalysisProcesses() {
        Path path = Paths.get("src/main/tests/files/bpel/sa-rules");
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        List<Test> result = new LinkedList<>();

        try {
            Files.walk(path, Integer.MAX_VALUE).forEach(dir -> {

                boolean isTestDirectory = hasFolderBpelFiles(dir);
                if (isTestDirectory) {
                    Path process = getBpelFileInFolder(dir);
                    String rule = getRule(process);
                    result.add(Capabilities.addMetrics(new Test(process,
                            FileTasks.getFilenameWithoutExtension(process),
                            Collections.singletonList(new BPELTestCase().checkFailedDeployment()),
                            new Feature(new FeatureSet(Groups.SA, rule), process.getFileName().toString()),
                            createXSDandWSDLPaths(dir), Collections.emptyList())));
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException("Could not walk folder " + path, e);
        }

        return result;
    }

    private static Path getBpelFileInFolder(Path dir) {
        try (Stream<Path> list = Files.list(dir)) {
            return list.filter(FileTypes::isBpelFile).findFirst().get();
        } catch (IOException e) {
            throw new IllegalStateException("could not find a bpel file in folder " + dir);
        }
    }

    private static boolean hasFolderBpelFiles(Path dir) {
        try (Stream<Path> list = Files.list(dir)) {
            return list.anyMatch(FileTypes::isBpelFile);
        } catch (IOException e) {
            // no BPEL files if the folder does not exist
            return false;
        }
    }

    static Map<String, List<Test>> getGroupsPerRuleForSAProcesses(List<Test> processes) {
        Map<String, List<Test>> result = new HashMap<>();

        IntStream.rangeClosed(1, 95).forEach((n) -> {
            String rule = convertIntegerToSARuleNumber(n);
            List<Test> processList = processes.stream().filter((p) -> p.getName().startsWith(rule)).collect(Collectors.toList());

            if (!processList.isEmpty()) {
                result.put(rule, processList);
            }
        });

        return result;
    }

    private static String getRule(Path process) {
        return IntStream.rangeClosed(1, 95).mapToObj(StaticAnalysisProcesses::convertIntegerToSARuleNumber).filter(n -> process.getFileName().toString().startsWith(n)).findFirst().orElse("UNKNOWN");
    }

    static String convertIntegerToSARuleNumber(int number) {
        return String.format("SA%05d", number);
    }

    private static List<Path> createXSDandWSDLPaths(Path dir) {
        try (Stream<Path> list = Files.list(dir)) {
            return list.filter(f -> FileTypes.isWsdlFile(f) || FileTypes.isXsdFile(f)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Could not open folder " + dir, e);
        }
    }

}

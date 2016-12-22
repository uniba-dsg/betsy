package configuration.bpel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import betsy.bpel.model.BPELTestCase;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import betsy.common.util.FileTypes;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import configuration.Capabilities;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.test.Test;

import static configuration.FilesLocation.BPEL_LOCATION;

class StaticAnalysisProcesses {

    static List<Test> STATIC_ANALYSIS = getStaticAnalysisProcesses();

    static List<Test> getStaticAnalysisProcesses() {
        Path path = Paths.get(BPEL_LOCATION).resolve("sa-rules");
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

                    final Optional<FeatureSet> featureSetOptional = Groups.SA.getFeatureSets().stream().filter(fs -> fs.getName().equals(rule)).findFirst();
                    final FeatureSet featureSet = featureSetOptional.orElseGet(() -> new FeatureSet(Groups.SA, rule));

                    // add tags
                    addTags(featureSet, rule);

                    final Feature feature = new Feature(featureSet, FileTasks.getFilenameWithoutExtension(process));


                    final Test test = new Test(process,
                            FileTasks.getFilenameWithoutExtension(process),
                            Collections.singletonList(new BPELTestCase().checkFailedDeployment()),
                            feature,
                            createXSDandWSDLPaths(dir), Collections.emptyList());

                    // add parent feature
                    test.addExtension("base", getBase(feature.getName()) + "__test");
                    test.addExtension("staticAnalysisChecks", "false");

                    result.add(Capabilities.addMetrics(test));
                }
            });

        } catch (IOException e) {
            throw new IllegalStateException("Could not walk folder " + path, e);
        }

        return result;
    }

    private static void addTags(FeatureSet theStaticAnalysisRule, String rule) {
        final List<String> lines = ClasspathHelper.getContentsForFileOfClasspath("/configuration/bpel/tag2rules.csv");

        Multimap<String, String> multimap = LinkedListMultimap.create();
        lines.forEach(line -> {
            String[] elems = line.split(",");
            String tag = elems[0];
            List<String> rules = Arrays.stream(elems[1].split(";")).map(x -> convertIntegerToSARuleNumber(Integer.parseInt(x.trim()))).collect(Collectors.toList());
            for (String r : rules) {
                multimap.put(r, tag);
            }
        });

        final Collection<String> tags = multimap.get(rule);
        if (tags.isEmpty()) {
            System.out.println("No tags found for " + rule);
        }
        theStaticAnalysisRule.addExtension("tags", String.join(", ", tags));
    }

    private static String getBase(String name) {
        final List<String> lines = ClasspathHelper.getContentsForFileOfClasspath("/configuration/bpel/mapping-satest2featuretest.csv");

        return lines.stream().filter(line -> line.startsWith(name + ",")).map(line -> line.split(",")[1]).findFirst().orElseGet(() -> {
            System.out.println("Could not find " + name);
            return "";
        });
    }

    private static Path getBpelFileInFolder(Path dir) {
        try (Stream<Path> list = Files.list(dir)) {
            return list.filter(FileTypes::isBpelFile).findFirst().orElseThrow(() -> new IllegalStateException("could not find bpel file in path " + dir));
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

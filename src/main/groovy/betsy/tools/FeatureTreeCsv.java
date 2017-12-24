package betsy.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import pebl.builder.Builder;
import pebl.xsd.PEBL;

public class FeatureTreeCsv {

    public static void main(String[] args) {
        writeFeatureTree(args[0]);
        writeFeatureTreeForFeatureSets(args[0]);
    }

    private static void writeFeatureTree(String arg) {
        PEBL pebl = Builder.getPebl();

        Path output = Paths.get(arg).resolve("feature-tree.csv");
        final List<String> lines = pebl.benchmark.tests.stream().map(t -> t.getFeature()).map(f -> String.join(";",
                f.getFeatureSet().getGroup().getLanguage().getCapability().getName(),
                f.getFeatureSet().getGroup().getLanguage().getName(),
                f.getFeatureSet().getGroup().getName(),
                f.getFeatureSet().getName(),
                f.getName()
        )).collect(Collectors.toList());
        try {
            Files.write(output, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeFeatureTreeForFeatureSets(String arg) {
        PEBL pebl = Builder.getPebl();

        Path output = Paths.get(arg).resolve("feature-set-tree.csv");
        final List<String> lines = pebl.benchmark.tests
                .stream()
                .map(t -> t.getFeature().getFeatureSet())
                .distinct()
                .map(f -> String.join(";",
                f.getGroup().getLanguage().getCapability().getName(),
                f.getGroup().getLanguage().getName(),
                f.getGroup().getName(),
                f.getName(),
                String.valueOf(f.getFeatures().size())
        )).collect(Collectors.toList());
        try {
            Files.write(output, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

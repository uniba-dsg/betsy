package loader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import pebl.aggregation.PEBLAggregator;
import org.xml.sax.SAXException;
import pebl.HasId;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Group;
import pebl.benchmark.feature.Language;
import pebl.result.test.TestResult;
import pebl.xsd.PEBL;

public class PEBLMergerMain {

    public static void main(String[] args) throws IOException, JAXBException, SAXException {
        Path peblSourcePath = Paths.get(args[0]);
        Path peblTargetPath = Paths.get(args[1]);

        System.out.println("Merging " + peblSourcePath + " into " + peblTargetPath);

        PEBL peblSource = PEBL.from(peblSourcePath);
        PEBL peblTarget = PEBL.from(peblTargetPath);

        merge(peblSource, peblTarget, peblTargetPath.toAbsolutePath().getParent());

        PEBLStoreFilesAlongMain.copyFilesRelative(peblTarget, peblTargetPath);
        PEBLBpmnPngImageAdderMain.createBpmnPngs(peblTarget, peblTargetPath);

        new PEBLAggregator().computeFeatureResults(peblTarget);

        peblTarget.writeTo(peblTargetPath.getParent());
    }

    private static void merge(PEBL peblSource, PEBL peblTarget, Path newRelativeDataFolder) {
        // Algorithm: add elements whose id was not yet there

        // add metric types if not yet available
        List<String> metricTypeIds = peblTarget.benchmark.metricTypes.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.benchmark.metricTypes.stream().filter(e -> !metricTypeIds.contains(e.getId())).forEach(e -> peblTarget.benchmark.metricTypes.add(e));

        // apply metrics
        peblSource.benchmark.capabilities.forEach(c -> {

            final Optional<Capability> capabilityOptional = peblTarget.benchmark.capabilities.stream().filter(x -> x.getId().equals(c.getId())).findFirst();

            if(!capabilityOptional.isPresent()) {
                peblTarget.benchmark.capabilities.add(c);
            } else {
                final Capability capability = capabilityOptional.get();
                c.getLanguages().forEach(l -> {

                    final Optional<Language> languageOptional = capability.getLanguages().stream().filter(x -> x.getId().equals(c.getId())).findFirst();
                    if(!languageOptional.isPresent()) {
                        capability.getLanguages().add(l);
                    } else {
                        final Language language = languageOptional.get();
                        l.getGroups().forEach(g -> {

                            final Optional<Group> groupOptional = language.getGroups().stream().filter(x -> x.getId().equals(c.getId())).findFirst();
                            if(!groupOptional.isPresent()) {
                                language.getGroups().add(g);
                            } else {
                                final Group group = groupOptional.get();

                                g.getFeatureSets().forEach(fs -> {

                                    final Optional<FeatureSet> featureSetOptional = group.getFeatureSets().stream().filter(x -> x.getId().equals(c.getId())).findFirst();
                                    if(!featureSetOptional.isPresent()) {
                                        group.getFeatureSets().add(fs);
                                    } else {
                                        final FeatureSet featureSet = featureSetOptional.get();

                                        fs.getFeatures().forEach(f -> {

                                            final Optional<Feature> featureOptional = featureSet.getFeatures().stream().filter(x -> x.getId().equals(c.getId())).findFirst();
                                            if(!featureOptional.isPresent()) {
                                                featureSet.getFeatures().add(f);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        // add tests if not yet available
        List<String> testIds = peblTarget.benchmark.tests.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.benchmark.tests.stream().filter(e -> !testIds.contains(e.getId())).forEach(e -> {
            final List<Path> files = e.getFiles().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());
            e.getFiles().clear();
            e.getFiles().addAll(files);

            e.setProcess(newRelativeDataFolder.relativize(e.getProcess()));

            peblTarget.benchmark.tests.add(e);
        });

        // add engines if not yet available
        List<String> engineIds = peblTarget.result.engines.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.result.engines.stream().filter(e -> !engineIds.contains(e.getId())).forEach(e -> peblTarget.result.engines.add(e));

        // add test results if not yet available
        List<String> testResultIds = peblTarget.result.testResults.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.result.testResults.forEach(e -> {
            final List<Path> files = e.getFiles().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());
            e.getFiles().clear();
            e.getFiles().addAll(files);

            final List<Path> logFiles = e.getLogFiles().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());
            e.getLogFiles().clear();
            e.getLogFiles().addAll(logFiles);

            final Optional<TestResult> testResultOptional = peblTarget.result.testResults.stream().filter(tr -> tr.getId().equals(e.getId())).findAny();
            if(testResultOptional.isPresent()) {
                final int index = peblTarget.result.testResults.indexOf(testResultOptional.get());
                peblTarget.result.testResults.set(index, e);
            } else {
                peblTarget.result.testResults.add(e);
            }
        });
    }

}

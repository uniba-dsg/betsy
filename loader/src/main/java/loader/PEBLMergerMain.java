package loader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;
import pebl.HasId;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.feature.FeatureSet;
import pebl.benchmark.feature.Group;
import pebl.benchmark.feature.Language;
import pebl.benchmark.test.Test;
import pebl.builder.Aggregator;
import pebl.result.Measurement;
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

        new Aggregator().computeFeatureResults(peblTarget);

        peblTarget.writeTo(peblTargetPath.getParent());
    }

    private static void merge(PEBL peblSource, PEBL peblTarget, Path newRelativeDataFolder) {
        // Algorithm: add elements whose id was not yet there

        // add metric types if not yet available
        System.out.println("MERGING metric types");
        List<String> metricTypeIds = peblTarget.benchmark.metricTypes.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.benchmark.metricTypes.stream().filter(e -> !metricTypeIds.contains(e.getId())).forEach(e -> {
            System.out.println("Adding metric type " + e.getId());
            peblTarget.benchmark.metricTypes.add(e);
        });

        // apply metrics
        System.out.println("MERGING feature tree");
        peblSource.benchmark.capabilities.forEach(c -> {

            final Optional<Capability> targetCapabilityOptional = peblTarget.benchmark.capabilities.stream().filter(x -> x.getId().equals(c.getId())).findFirst();

            if(!targetCapabilityOptional.isPresent()) {
                System.out.println("Adding capability " + c.getId());
                peblTarget.benchmark.capabilities.add(c);
            } else {
                final Capability targetCapability = targetCapabilityOptional.get();
                c.getLanguages().forEach(l -> {

                    final Optional<Language> languageOptional = targetCapability.getLanguages().stream().filter(x -> x.getId().equals(l.getId())).findFirst();
                    if(!languageOptional.isPresent()) {
                        System.out.println("Adding language " + l.getId());
                        targetCapability.getLanguages().add(l);
                    } else {
                        final Language language = languageOptional.get();
                        l.getGroups().forEach(g -> {

                            final Optional<Group> groupOptional = language.getGroups().stream().filter(x -> x.getId().equals(g.getId())).findFirst();
                            if(!groupOptional.isPresent()) {
                                System.out.println("Adding new group " + g.getId());
                                language.getGroups().add(g);
                            } else {
                                final Group group = groupOptional.get();

                                g.getFeatureSets().forEach(fs -> {

                                    final Optional<FeatureSet> featureSetOptional = group.getFeatureSets().stream().filter(x -> x.getId().equals(fs.getId())).findFirst();
                                    if(!featureSetOptional.isPresent()) {
                                        System.out.println("Adding new feature set " + fs.getId());
                                        group.getFeatureSets().add(fs);
                                    } else {
                                        final FeatureSet featureSet = featureSetOptional.get();

                                        fs.getFeatures().forEach(f -> {

                                            final Optional<Feature> featureOptional = featureSet.getFeatures().stream().filter(x -> x.getId().equals(f.getId())).findFirst();
                                            if(!featureOptional.isPresent()) {
                                                System.out.println("Adding new feature " + f.getId());
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
        System.out.println("MERGING tests");
        mergeTests(peblSource, peblTarget, newRelativeDataFolder);

        // add engines if not yet available
        System.out.println("MERGING engines");
        mergeEngines(peblSource, peblTarget);

        // add test results if not yet available
        System.out.println("MERGING test results");
        mergeTestResults(peblSource, peblTarget, newRelativeDataFolder);
    }

    private static void mergeTests(PEBL peblSource, PEBL peblTarget, Path newRelativeDataFolder) {
        List<String> testIds = peblTarget.benchmark.tests.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.benchmark.tests.stream().filter(e -> !testIds.contains(e.getId())).forEach(e -> {
            final List<Path> files = e.getFiles().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());

            peblTarget.benchmark.tests.add(new Test(
                    newRelativeDataFolder.relativize(e.getProcess()),
                    e.getTestCases(),
                    peblTarget.getFeature(e.getFeature().getId()),
                    files,
                    e.getDescription(),
                    e.getTestPartners(),
                    e.getExtensions()
            ));
        });
    }

    private static void mergeEngines(PEBL peblSource, PEBL peblTarget) {
        List<String> engineIds = peblTarget.result.engines.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.result.engines.stream().filter(e -> !engineIds.contains(e.getId())).forEach(e -> {
            peblTarget.result.engines.add(e);
        });
    }

    private static void mergeTestResults(PEBL peblSource, PEBL peblTarget, Path newRelativeDataFolder) {
        List<String> testResultIds = peblTarget.result.testResults.stream().map(HasId::getId).collect(Collectors.toList());
        peblSource.result.testResults.forEach(e -> {

            final List<Path> files = e.getFiles().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());
            final List<Path> logFiles = e.getLogs().stream().map(newRelativeDataFolder::relativize).collect(Collectors.toList());
            Test test = peblTarget.getTest(e.getTest().getId());
            TestResult newTestResult = new TestResult(
                    test,
                    peblTarget.getEngine(e.getEngine().getId()),
                    e.getTool(),
                    logFiles,
                    e.getDeploymentPackage(),
                    files,
                    e.getMeasurements().stream().map(m -> new Measurement(peblTarget.getMetric(m.getMetric().getId()), m.getValue())).collect(Collectors.toList()),
                    e.getExtensions(),
                    e.getTestCaseResults()
            );


            final Optional<TestResult> testResultOptional = peblTarget.result.testResults.stream().filter(tr -> tr.getId().equals(e.getId())).findAny();
            if(testResultOptional.isPresent()) {
                final int index = peblTarget.result.testResults.indexOf(testResultOptional.get());
                peblTarget.result.testResults.set(index, newTestResult);
            } else {
                peblTarget.result.testResults.add(newTestResult);
            }
        });
    }

}

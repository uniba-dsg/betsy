package pebl.xsd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import pebl.benchmark.feature.Capability;
import pebl.benchmark.feature.MetricType;
import pebl.benchmark.test.Test;
import pebl.result.engine.Engine;
import pebl.result.feature.AggregatedResult;
import pebl.result.test.TestResult;

@XmlRootElement
public class PEBL {

    public static PEBL from(Path path) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        PEBL pebl;
        if(path.endsWith(".json")) {
            unmarshaller.setProperty("eclipselink.media-type", "application/json");
            unmarshaller.setProperty(UnmarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            pebl =  (PEBL) unmarshaller.unmarshal(path.toFile());
        } else {
            pebl = (PEBL) unmarshaller.unmarshal(path.toFile());
        }
        pebl.makeFilesAbsolute(path.toAbsolutePath().getParent());

        //TODO reenable link assertion
        //pebl.assertLinksWork();

        return pebl;
    }

    private void toJson(Path path) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        Marshaller m4json = jc.createMarshaller();
        m4json.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m4json.setProperty("eclipselink.media-type", "application/json");
        m4json.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
        m4json.marshal(this, path.toFile());
    }

    private void toXml(Path path) throws IOException {
        JAXB.marshal(this, path.toFile());
    }

    public void assertLinksWork() {
        List<Path> unlinked = new LinkedList<>();

        Predicate<Path> relative = f -> !f.toAbsolutePath().toString().equals(f.toString());
        Predicate<Path> missing = f -> !Files.exists(f);

        benchmark.tests.forEach(e -> {
            e.getFiles().stream().filter(missing.or(relative)).forEach(unlinked::add);
            Optional.of(e.getProcess()).filter(missing.or(relative)).ifPresent(unlinked::add);
        });
        result.testResults.forEach(e -> {
            e.getFiles().stream().filter(missing.or(relative)).forEach(unlinked::add);
            e.getLogs().stream().filter(missing.or(relative)).forEach(unlinked::add);
        });

        if(unlinked.size() > 0) {
            throw new IllegalStateException("Unlinked files: " + unlinked);
        }
    }

    public void writeTo(Path workingDirectory) {
        try {
            Files.createDirectories(workingDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        makeFilesRelative(workingDirectory.toAbsolutePath());

        try {
            toJson(workingDirectory.resolve("pebl.json"));
            toXml(workingDirectory.resolve("pebl.xml"));
        } catch (IOException | JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeFilesAbsolute(Path workingDirectory) {
        benchmark.tests.forEach(e -> {
            final List<Path> files = e.getFiles()
                    .stream()
                    .map(workingDirectory::resolve)
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
            e.getFiles().clear();
            e.getFiles().addAll(files);

            e.setProcess(workingDirectory.resolve(e.getProcess()).toAbsolutePath());
        });

        result.testResults.forEach(e -> {
            final List<Path> files = e.getFiles()
                    .stream()
                    .map(workingDirectory::resolve)
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
            e.getFiles().clear();
            e.getFiles().addAll(files);

            final List<Path> logFiles = e.getLogs()
                    .stream()
                    .map(workingDirectory::resolve)
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
            e.getLogs().clear();
            e.getLogs().addAll(logFiles);
        });
    }

    public void makeFilesRelative(Path workingDirectory) {
        benchmark.tests.forEach(e -> {
            final List<Path> files = new LinkedList<>();

            for(Path file : e.getFiles()) {
                // requires file to be absolute
                Path relativeFile = workingDirectory.relativize(file.toAbsolutePath());
                files.add(relativeFile);
            }

            e.getFiles().clear();
            e.getFiles().addAll(files);

            e.setProcess(workingDirectory.relativize(e.getProcess().toAbsolutePath()));
        });

        result.testResults.forEach(e -> {
            final List<Path> files = e.getFiles()
                    .stream()
                    .map(Path::toAbsolutePath)
                    .map(workingDirectory::relativize)
                    .collect(Collectors.toList());
            e.getFiles().clear();
            e.getFiles().addAll(files);

            final List<Path> logFiles = e.getLogs()
                    .stream()
                    .map(Path::toAbsolutePath)
                    .map(workingDirectory::relativize)
                    .collect(Collectors.toList());
            e.getLogs().clear();
            e.getLogs().addAll(logFiles);
        });
    }

    public static class Benchmark {

        @XmlElement(name = "capability")
        @XmlElementWrapper(name = "capabilities")
        public List<Capability> capabilities = new LinkedList<>();

        @XmlElement(name = "test")
        @XmlElementWrapper(name = "tests")
        public List<Test> tests = new LinkedList<>();

        @XmlElement(name = "metricType")
        @XmlElementWrapper(name = "metricTypes")
        public List<MetricType> metricTypes = new LinkedList<>();

    }

    public static class Result {

        @XmlElement(name = "engine")
        @XmlElementWrapper(name = "engines")
        public List<Engine> engines = new LinkedList<>();

        @XmlElement(name = "testResult")
        @XmlElementWrapper(name = "testResults")
        public List<TestResult> testResults = new LinkedList<>();

        @XmlElement(name = "aggregatedResult")
        @XmlElementWrapper(name = "aggregatedResults")
        public List<AggregatedResult> aggregatedResults = new LinkedList<>();

    }

    @XmlElement(required = true)
    public Benchmark benchmark = new Benchmark();

    @XmlElement(required = true)
    public Result result = new Result();

}

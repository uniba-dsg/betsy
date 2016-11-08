package betsy.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpel.virtual.host.VirtualEngineAPI;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.engines.EngineLifecycle;
import betsy.common.model.engine.EngineExtended;
import betsy.common.model.engine.IsEngine;
import betsy.common.util.GitUtil;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;
import pebl.result.engine.Engine;
import pebl.benchmark.feature.Feature;
import pebl.benchmark.test.Test;
import pebl.result.tool.Tool;
import pebl.xsd.Engines;
import pebl.xsd.Features;
import pebl.xsd.PEBL;
import pebl.xsd.Tests;
import pebl.xsd.Tools;

public class XmlMain {

    public static void main(String[] args) throws IOException, JAXBException {
        Path workingDirectory = Paths.get(".");
        generateEnginesXml(workingDirectory);
        generateFeaturesXml(workingDirectory);
        generateTestsXml(workingDirectory);
        generateToolsXml(workingDirectory);
        generatePeblXml(workingDirectory);
        generatePeblJson(workingDirectory);
    }

    public static void generatePeblJson(Path workingDirectory) throws IOException, JAXBException {
        PEBL pebl = getPebl();

        Path targetFile = workingDirectory.resolve("pebl.json");

        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        Marshaller m4json= jc.createMarshaller();
        m4json.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m4json.setProperty("eclipselink.media-type", "application/json");
        m4json.marshal(pebl, targetFile.toFile());
    }

    public static void generatePeblXml(Path workingDirectory) throws IOException {
        PEBL pebl = getPebl();

        Path targetFile = workingDirectory.resolve("pebl.xml");
        JAXB.marshal(pebl, targetFile.toFile());
    }

    public static PEBL getPebl() {
        PEBL pebl = new PEBL();
        pebl.result.tools.addAll(getTools());
        pebl.result.engines.addAll(getEngines());
        pebl.benchmark.tests.addAll(getTests().stream().collect(Collectors.toList()));
        pebl.benchmark.capabilities.addAll(new Features(getFeatures()).capabilities);
        return pebl;
    }

    private static void generateToolsXml(Path workingDirectory) throws IOException {
        Tools tools = new Tools(getTools());

        Path targetFile = workingDirectory.resolve("tools.xml");
        JAXB.marshal(tools, targetFile.toFile());
    }

    private static void generateFeaturesXml(Path workingDirectory) throws IOException {
        Features features = new Features(getFeatures());

        Path targetFile = workingDirectory.resolve("features.xml");
        JAXB.marshal(features, targetFile.toFile());
    }

    private static List<Feature> getFeatures() {
        return getTests().stream().map(Test::getFeature).distinct().collect(Collectors.toList());
    }

    private static void generateTestsXml(Path workingDirectory) throws IOException {
        Tests tests = new Tests(getTests());

        Path targetFile = workingDirectory.resolve("tests.xml");
        JAXB.marshal(tests, targetFile.toFile());
    }

    private static void generateEnginesXml(Path workingDirectory) throws IOException {
        Engines engines = new Engines(getEngines());

        Path targetFile = workingDirectory.resolve("engines.xml");
        JAXB.marshal(engines, targetFile.toFile());
    }

    private static List<Engine> getEngines() {
        return getEngineLifecycles().stream()
                .filter(e -> !(e instanceof VirtualEngineAPI))
                .map(e -> (IsEngine) e)
                .map(IsEngine::getEngineObject)
                .map(EngineExtended::getEngine)
                .collect(Collectors.toList());
    }

    private static List<EngineLifecycle> getEngineLifecycles() {
        final List<EngineLifecycle> bpelEngines = new BPELEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> bpmnEngines = new BPMNEngineRepository().getByName("ALL").stream().collect(Collectors.toList());
        final List<EngineLifecycle> engines = new LinkedList<>();
        engines.addAll(bpelEngines);
        engines.addAll(bpmnEngines);

        return engines;
    }

    private static List<Test> getTests() {
        List<Test> processes = new LinkedList<>();
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ALL"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("ERRORS"));
        processes.addAll(BPELProcessRepository.INSTANCE.getByName("STATIC_ANALYSIS"));
        processes.addAll(new BPMNProcessRepository().getByName("ALL"));
        return processes;
    }

    private static List<Tool> getTools() {
        return Collections.singletonList(new Tool("betsy", GitUtil.getGitCommit()));
    }

}

package betsy.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import betsy.tools.pebl.PEBLBuilder;
import pebl.xsd.Engines;
import pebl.xsd.Features;
import pebl.xsd.PEBL;
import pebl.xsd.Tests;
import pebl.xsd.Tools;

public class XmlMain {

    public static void main(String[] args) throws IOException, JAXBException {
        writeInDirectory(Paths.get("."));
    }

    public static void writeInDirectory(Path workingDirectory) {
        try {
            generateEnginesXml(workingDirectory);
            generateFeaturesXml(workingDirectory);
            generateTestsXml(workingDirectory);
            generateToolsXml(workingDirectory);

            PEBL pebl = PEBLBuilder.getPebl();
            pebl.writeTo(workingDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateToolsXml(Path workingDirectory) throws IOException {
        Tools tools = new Tools(PEBLBuilder.getTools());

        Path targetFile = workingDirectory.resolve("tools.xml");
        JAXB.marshal(tools, targetFile.toFile());
    }

    private static void generateFeaturesXml(Path workingDirectory) throws IOException {
        Features features = new Features(PEBLBuilder.getFeatures());

        Path targetFile = workingDirectory.resolve("features.xml");
        JAXB.marshal(features, targetFile.toFile());
    }

    private static void generateTestsXml(Path workingDirectory) throws IOException {
        Tests tests = new Tests(PEBLBuilder.getTests());

        Path targetFile = workingDirectory.resolve("tests.xml");
        JAXB.marshal(tests, targetFile.toFile());
    }

    private static void generateEnginesXml(Path workingDirectory) throws IOException {
        Engines engines = new Engines(PEBLBuilder.getEngines());

        Path targetFile = workingDirectory.resolve("engines.xml");
        JAXB.marshal(engines, targetFile.toFile());
    }

}

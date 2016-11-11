package betsy.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import betsy.tools.pebl.PEBLBuilder;
import org.junit.Test;
import pebl.xsd.PEBL;

import static org.junit.Assert.assertEquals;

public class XmlMainTest {

    @Test
    public void roundTripXml() throws IOException {
        Path tempDirectory = Files.createTempDirectory("betsy-pebl-serialization");
        System.out.println(tempDirectory);

        Path round1 = tempDirectory.resolve("round1");
        Files.createDirectories(round1);

        Path round1peblxml = round1.resolve("pebl.xml");
        PEBL pebl = PEBLBuilder.getPebl();
        JAXB.marshal(pebl, round1peblxml.toFile());
        PEBL peblFromXml = JAXB.unmarshal(round1peblxml.toFile(), PEBL.class);

        Path round2 = tempDirectory.resolve("round2");
        Files.createDirectories(round2);
        Path round2peblxml = round2.resolve("pebl.xml");
        JAXB.marshal(peblFromXml, round2peblxml.toFile());

        assertEquals(
                String.join("\n", Files.readAllLines(round1peblxml)),
                String.join("\n", Files.readAllLines(round2peblxml))
        );
    }

    @Test
    public void roundTripJson() throws IOException, JAXBException {
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty("eclipselink.media-type", "application/json");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");


        Path tempDirectory = Files.createTempDirectory("betsy-pebl-serialization");
        System.out.println(tempDirectory);

        Path round1 = tempDirectory.resolve("round1");
        Files.createDirectories(round1);

        Path round1peblJson = round1.resolve("pebl.json");
        marshaller.marshal(PEBLBuilder.getPebl(), round1peblJson.toFile());
        PEBL peblFromJson = (PEBL) unmarshaller.unmarshal(round1peblJson.toFile());

        Path round2 = tempDirectory.resolve("round2");
        Files.createDirectories(round2);
        Path round2peblJson = round2.resolve("pebl.json");
        marshaller.marshal(peblFromJson, round2peblJson.toFile());

        assertEquals(
                String.join("\n", Files.readAllLines(round1peblJson)),
                String.join("\n", Files.readAllLines(round2peblJson))
        );
    }

}

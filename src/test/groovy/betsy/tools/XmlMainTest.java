package betsy.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXB;

import org.junit.Test;
import pebl.xsd.PEBL;

import static org.junit.Assert.assertEquals;

public class XmlMainTest {

    @Test
    public void roundTrip() throws IOException {
        Path tempDirectory = Files.createTempDirectory("betsy-pebl-serialization");
        System.out.println(tempDirectory);

        Path round1 = tempDirectory.resolve("round1");
        Files.createDirectories(round1);
        XmlMain.generatePeblXml(round1);

        Path round1peblxml = round1.resolve("pebl.xml");
        PEBL peblXml = JAXB.unmarshal(round1peblxml.toFile(), PEBL.class);

        Path round2 = tempDirectory.resolve("round2");
        Files.createDirectories(round2);
        Path round2peblxml = round2.resolve("pebl.xml");
        JAXB.marshal(peblXml, round2peblxml.toFile());

        assertEquals(
                String.join("\n", Files.readAllLines(round1peblxml)),
                String.join("\n", Files.readAllLines(round2peblxml))
        );
    }

}

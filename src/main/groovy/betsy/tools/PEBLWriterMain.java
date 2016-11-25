package betsy.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import betsy.tools.pebl.PEBLBuilder;
import org.xml.sax.SAXException;
import pebl.xsd.PEBL;
import pebl.xsd.SchemaGenerator;

public class PEBLWriterMain {

    public static void main(String[] args) throws IOException, JAXBException, SAXException {
        Path workingDirectory = Paths.get(args[0]);
        writeInDirectory(workingDirectory);
        System.out.println("Written pebl to disk");
        validateXml(workingDirectory);
        System.out.println("Validated against XSD");
    }

    public static void writeInDirectory(Path workingDirectory) {
        PEBL pebl = PEBLBuilder.getPebl();
        pebl.writeTo(workingDirectory);
    }

    public static void validateXml(Path workingDirectory) throws JAXBException, IOException, SAXException {
        Path xsd = Paths.get("pebl/src/main/resources/pebl/pebl.xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Source schemaFile = new StreamSource(xsd.toFile());
        Schema schema = factory.newSchema(schemaFile);

        Validator validator = schema.newValidator();

        validator.validate(new StreamSource(workingDirectory.resolve("pebl.xml").toFile()));
    }

}

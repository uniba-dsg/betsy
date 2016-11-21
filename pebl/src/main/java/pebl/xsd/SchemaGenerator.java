package pebl.xsd;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Generates the XSD of PEBL
 */
public class SchemaGenerator {

    public static void main(String[] args) throws JAXBException, IOException {
        generateXSD();
        generateJsonSchema();
    }

    public static Path generateJsonSchema() throws JAXBException {
        File jsonSchema = new File("pebl/src/main/resources/pebl/pebl.json");
        org.eclipse.persistence.jaxb.JAXBContext c = (org.eclipse.persistence.jaxb.JAXBContext) JAXBContext.newInstance(PEBL.class);
        c.generateJsonSchema(new SchemaOutputResolver() {

            public Result createOutput(String uri, String suggestedFileName) throws IOException {
                System.out.println(uri);

                File file = jsonSchema;
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }

        }, PEBL.class);

        return jsonSchema.toPath();
    }

    public static Path generateXSD() throws JAXBException, IOException {
        File xsd = new File("pebl/src/main/resources/pebl/pebl.xsd");
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        SchemaOutputResolver sor = new SchemaOutputResolver() {

            public Result createOutput(String uri, String suggestedFileName) throws IOException {
                System.out.println(uri);
                File file = xsd;
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }

        };
        jc.generateSchema(sor);
        return xsd.toPath();
    }

}

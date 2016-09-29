package pebl.xsd;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.internal.jaxb.json.schema.JsonSchemaGenerator;

/**
 * Generates the XSD of PEBL
 */
public class SchemaGenerator {

    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(PEBL.class);
        SchemaOutputResolver sor = new SchemaOutputResolver() {

            public Result createOutput(String uri, String suggestedFileName) throws IOException {
                System.out.println(uri);
                File file = new File("pebl/src/main/resources/schema/pebl.xsd");
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }

        };
        jc.generateSchema(sor);

        org.eclipse.persistence.jaxb.JAXBContext c = (org.eclipse.persistence.jaxb.JAXBContext) jc;
        c.generateJsonSchema(new SchemaOutputResolver() {

            public Result createOutput(String uri, String suggestedFileName) throws IOException {
                System.out.println(uri);
                File file = new File("pebl/src/main/resources/schema/pebl.json");
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }

        }, PEBL.class);
    }

}

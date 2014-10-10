package betsy.bpmn;

import betsy.bpmn.cli.BPMNProcessParser;
import betsy.bpmn.model.BPMNNamespaceContext;
import betsy.bpmn.model.BPMNProcess;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by joerg on 10.10.2014.
 */
public class ProcessValidator {

    List<BPMNProcess> processes;

    public void validate() {
        String[] args = {"ALL"};
        processes = new BPMNProcessParser(args).parse();
        assertNamingConventionsCorrect();
    }

    private void assertNamingConventionsCorrect() {

        for (BPMNProcess process : processes) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(process.getResourceFile().toString());
                XPathFactory xPathfactory = XPathFactory.newInstance();
                XPath xpath = xPathfactory.newXPath();
                xpath.setNamespaceContext(new BPMNNamespaceContext());
                XPathExpression expr = xpath.compile("//*[local-name() = 'definitions' and @id='" + process.getName() + "Test']");
                NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
                if (nodeList.getLength() != 1) {
                    throw new IllegalStateException("No definitions element with id '" + process.getName() + "Test' found in process " + process.getName());
                }
            } catch (ParserConfigurationException | SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }

    }
}

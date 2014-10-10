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
import java.util.List;

public class ProcessValidator {

    private List<BPMNProcess> processes;

    private DocumentBuilder builder;

    private XPath xpath;

    public void validate() {
        String[] args = {"ALL"};
        processes = new BPMNProcessParser(args).parse();
        assertNamingConventionsCorrect();
    }

    private void assertNamingConventionsCorrect() {
        try {
            setUpXmlObjects();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not validate process files",e);
        }

        for (BPMNProcess process : processes) {
            try {
                Document doc = builder.parse(process.getResourceFile().toString());

                XPathExpression definitionExpression = xpath.compile("//*[local-name() = 'definitions' and @id='" + process.getName() + "Test']");
                NodeList nodeList = (NodeList) definitionExpression.evaluate(doc, XPathConstants.NODESET);
                if (nodeList.getLength() != 1) {
                    throw new IllegalStateException("No definitions element with id '" + process.getName() + "Test' found in process " + process.getName());
                }

                XPathExpression processExpression = xpath.compile("//*[local-name() = 'process' and @id='" + process.getName() + "']");
                nodeList = (NodeList) processExpression.evaluate(doc, XPathConstants.NODESET);
                if (nodeList.getLength() != 1) {
                    throw new IllegalStateException("No process element with id '" + process.getName() + "' found in process " + process.getName());
                }

            } catch ( SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }


    }

    private void setUpXmlObjects() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        XPathFactory xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new BPMNNamespaceContext());
    }
}

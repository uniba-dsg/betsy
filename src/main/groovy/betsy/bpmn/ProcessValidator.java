package betsy.bpmn;

import betsy.bpmn.model.BPMNNamespaceContext;
import betsy.bpmn.model.BPMNProcess;
import configuration.bpmn.BPMNProcessRepository;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.*;

public class ProcessValidator {

    private List<BPMNProcess> processes;

    private DocumentBuilder builder;

    private XPath xpath;

    public static final String[] ALLOWED_ASSERTIONS = new String[]{
            "notInterrupted", "called", "errorTask", "timerInternal", "multi", "timerEvent", "default", "timerExternal",
            "signaled", "lane2", "end", "lane1", "started", "task1", "CREATE_LOG_FILE", "task2",
            "task3", "false", "subprocess", "normalTask", "interrupted", "condition", "success",
            "true", "compensate", "transaction"
    };

    public void validate() {
        processes = new BPMNProcessRepository().getByName("ALL");

        setUpXmlObjects();
        assertNamingConventionsCorrect();
        assertAssertionsCorrect();
    }

    private void assertNamingConventionsCorrect() {
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

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }
    }

    private void assertAssertionsCorrect() {
        Set<String> assertions = new HashSet<>();

        for (BPMNProcess process : processes) {
            try {
                Document doc = builder.parse(process.getResourceFile().toString());

                XPathExpression definitionExpression = xpath.compile("//*[local-name() = 'script']");
                NodeList nodeList = (NodeList) definitionExpression.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    String textContent = nodeList.item(i).getTextContent();

                    if(!Arrays.asList(ALLOWED_ASSERTIONS).contains(textContent)) {
                        System.out.println(textContent + " in " + process.getResourceFile());
                    }

                    if(textContent.contains(",")){
                        Collections.addAll(assertions, textContent.split(","));
                    } else {
                        assertions.add(textContent);
                    }
                }

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }

        // TODO move this to the production code

        String[] actualAssertions = assertions.toArray(new String[assertions.size()]);

        Arrays.sort(actualAssertions);
        Arrays.sort(ALLOWED_ASSERTIONS);

        System.out.println(Arrays.toString(ALLOWED_ASSERTIONS));
        System.out.println(Arrays.toString(actualAssertions));

        Assert.assertArrayEquals(ALLOWED_ASSERTIONS, actualAssertions);
    }

    private void setUpXmlObjects() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            XPathFactory xPathfactory = XPathFactory.newInstance();
            xpath = xPathfactory.newXPath();
            xpath.setNamespaceContext(new BPMNNamespaceContext());
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not validate process files", e);
        }
    }
}

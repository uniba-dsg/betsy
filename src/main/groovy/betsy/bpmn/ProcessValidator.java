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

    public static final String[] ALLOWED_LOG_MESSAGES = new String[]{
            "SCRIPT_taskNotInterrupted", "SCRIPT_timerInternal", "SCRIPT_timerEvent", "SCRIPT_default", "SCRIPT_timerExternal",
            "SCRIPT_signaled", "SCRIPT_end", "SCRIPT_started", "SCRIPT_task1", "CREATE_LOG_FILE", "SCRIPT_task2",
            "SCRIPT_task3", "SCRIPT_task4", "SCRIPT_false", "SCRIPT_subprocess", "SCRIPT_normalTask", "SCRIPT_interrupted", "SCRIPT_success",
            "SCRIPT_true", "SCRIPT_transaction"
    };

    public void validate() {
        processes = new BPMNProcessRepository().getByName("ALL");

        setUpXmlObjects();
        assertNamingConventionsCorrect();
        assertLogMessagesCorrect();
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

                XPathExpression targetNamespaceExpression = xpath.compile("//*[local-name() = 'definitions' and @targetNamespace =  'http://dsg.wiai.uniba.de/betsy/bpmn/" + process.getName().substring(0, 1).toLowerCase() + process.getName().substring(1, process.getName().length()) + "']");
                nodeList = (NodeList) targetNamespaceExpression.evaluate(doc, XPathConstants.NODESET);
                if (nodeList.getLength() != 1) {
                    throw new IllegalStateException("targetNamespace of definitions element of process '" + process.getName() + " is not http://dsg.wiai.uniba.de/betsy/bpmn/" + process.getName().substring(0, 1).toLowerCase() + process.getName().substring(1, process.getName().length()));
                }

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }
    }

    private void assertLogMessagesCorrect() {
        Set<String> messages = new HashSet<>();

        for (BPMNProcess process : processes) {
            try {
                Document doc = builder.parse(process.getResourceFile().toString());

                XPathExpression definitionExpression = xpath.compile("//*[local-name() = 'script']");
                NodeList nodeList = (NodeList) definitionExpression.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    String textContent = nodeList.item(i).getTextContent();

                    if (textContent.contains(",")) {
                        for (String x : textContent.split(",")) {
                            addMessage(messages, process, x);
                        }
                    } else {
                        addMessage(messages, process, textContent);
                    }
                }

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getResourceFile().toString(), e);
            }
        }

        // TODO move this to the production code

        String[] actualMessages = messages.toArray(new String[messages.size()]);

        Arrays.sort(actualMessages);
        Arrays.sort(ALLOWED_LOG_MESSAGES);
        if(actualMessages.length<ALLOWED_LOG_MESSAGES.length) {
            List<String> allowedMsgList = new LinkedList<>(Arrays.asList(ALLOWED_LOG_MESSAGES));
            allowedMsgList.removeAll(Arrays.asList(actualMessages));
            System.out.println("Allowed Log Messages "+allowedMsgList+" are not used anymore.");
        } else if(actualMessages.length>ALLOWED_LOG_MESSAGES.length) {
            List<String> actualMsgList = new LinkedList<>(Arrays.asList(actualMessages));
            actualMsgList.removeAll(Arrays.asList(ALLOWED_LOG_MESSAGES));
            System.out.println("Log Messages "+actualMsgList+" are used but not allowed anymore.");
        }
        Assert.assertArrayEquals(ALLOWED_LOG_MESSAGES, actualMessages);
    }

    private void addMessage(Set<String> assertions, BPMNProcess process, String x) {
        if (!Arrays.asList(ALLOWED_LOG_MESSAGES).contains(x)) {
            System.out.println(x + " in " + process.getResourceFile());
        }

        assertions.add(x);
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

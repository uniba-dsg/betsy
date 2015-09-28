package betsy.bpmn.validation;

import betsy.bpmn.model.BPMNAssertions;
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

    private static final String[] ALLOWED_LOG_MESSAGES = getAllowedLogMessages();

    public void validate() {
        processes = new BPMNProcessRepository().getByName("ALL");

        setUpXmlObjects();
        assertNamingConventionsCorrect();
        assertLogMessagesCorrect();
    }

    private void assertNamingConventionsCorrect() {
        for (BPMNProcess process : processes) {
            try {
                Document doc = builder.parse(process.getProcess().toString());

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

                String expectedTargetNamespace = "http://dsg.wiai.uniba.de/betsy/bpmn/"+process.getName();
                XPathExpression targetNamespaceExpression = xpath.compile("//*[local-name() = 'definitions']/@targetNamespace");
                nodeList = (NodeList) targetNamespaceExpression.evaluate(doc, XPathConstants.NODESET);
                if (nodeList.getLength() != 1 && expectedTargetNamespace.toLowerCase().equals(nodeList.item(0).getTextContent().toLowerCase())) {
                    throw new IllegalStateException("targetNamespace of definitions element of process '" + process.getName() + " is not '"+expectedTargetNamespace+"'");
                }

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getProcess().toString(), e);
            }
        }
    }

    private void assertLogMessagesCorrect() {
        Set<String> messages = new HashSet<>();

        for (BPMNProcess process : processes) {
            try {
                Document doc = builder.parse(process.getProcess().toString());

                XPathExpression definitionExpression = xpath.compile("//*[local-name() = 'script']");
                NodeList nodeList = (NodeList) definitionExpression.evaluate(doc, XPathConstants.NODESET);
                for (int i = 0; i < nodeList.getLength(); i++) {
                    String textContent = nodeList.item(i).getTextContent();

                    addMessage(messages, process, textContent);
                }

            } catch (SAXException | XPathExpressionException | IOException e) {
                throw new IllegalStateException("Validation failed for file " + process.getProcess().toString(), e);
            }
        }

        String[] actualMessages = messages.toArray(new String[messages.size()]);

        Arrays.sort(actualMessages);
        Arrays.sort(ALLOWED_LOG_MESSAGES);
        if (actualMessages.length < ALLOWED_LOG_MESSAGES.length) {
            List<String> allowedMsgList = new LinkedList<>(Arrays.asList(ALLOWED_LOG_MESSAGES));
            allowedMsgList.removeAll(Arrays.asList(actualMessages));
            System.out.println("Allowed Log Messages " + allowedMsgList + " are not used anymore.");
        } else if (actualMessages.length > ALLOWED_LOG_MESSAGES.length) {
            List<String> actualMsgList = new LinkedList<>(Arrays.asList(actualMessages));
            actualMsgList.removeAll(Arrays.asList(ALLOWED_LOG_MESSAGES));
            System.out.println("Log Messages " + actualMsgList + " are used but not allowed anymore.");
        }
        Assert.assertArrayEquals(ALLOWED_LOG_MESSAGES, actualMessages);
    }

    private void addMessage(Set<String> assertions, BPMNProcess process, String x) {
        if (!Arrays.asList(ALLOWED_LOG_MESSAGES).contains(x)) {
            System.out.println(x + " in " + process.getProcess());
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

    public static String[] getAllowedLogMessages() {
        List<String> allowedScriptTasks = BPMNAssertions.getScriptAssertions();
        List<String> allowedLogMessages = new LinkedList<>();
        allowedLogMessages.addAll(allowedScriptTasks);
        allowedLogMessages.add("CREATE_LOG_FILE");
        allowedLogMessages.add("INC_COUNTER");
        allowedLogMessages.add("SET_COUNTER");
        allowedLogMessages.add("CREATE_TIMESTAMP_LOG_1");
        allowedLogMessages.add("CREATE_TIMESTAMP_LOG_2");
        allowedLogMessages.add("WAIT_TEN_SECONDS");
        allowedLogMessages.add("SET_STRING_DATA");
        allowedLogMessages.add("LOG_DATA");
        allowedLogMessages.add("THROW_ERROR");
        return allowedLogMessages.toArray(new String[allowedLogMessages.size()]);
    }

}

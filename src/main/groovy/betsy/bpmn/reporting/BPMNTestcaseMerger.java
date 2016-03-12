package betsy.bpmn.reporting;

import betsy.common.tasks.FileTasks;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class BPMNTestcaseMerger {

    static class MergeResult {
        String name = "";
        int errors;
        int failures;
        int skipped;
        int tests;
        double time;
        List<Node> testCases = new ArrayList<>();
        Node properties;
    }


    private final Path reportPath;

    public BPMNTestcaseMerger(Path reportPath) {
        this.reportPath = reportPath;
    }

    public void mergeTestCases() {

        // remove "Test-" from the file names
        removePrefixOfTestResults(reportPath);

        //information needed for the generated test suite
        MergeResult result = new MergeResult();

        //extract information
        DocumentBuilder dBuilder = createDocumentBuilder();

        try {
            Files.walkFileTree(reportPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Document document = parseFile(file, dBuilder);
                    document.getDocumentElement().normalize();
                    Element testSuite = document.getDocumentElement();
                    result.name = testSuite.getAttribute("name");
                    result.errors += Integer.parseInt(testSuite.getAttribute("errors"));
                    result.failures += Integer.parseInt(testSuite.getAttribute("failures"));
                    result.skipped += Integer.parseInt(testSuite.getAttribute("skipped"));
                    result.tests += Integer.parseInt(testSuite.getAttribute("tests"));
                    result.time += Double.parseDouble(testSuite.getAttribute("time"));
                    result.testCases.add(testSuite.getElementsByTagName("testcase").item(0));
                    result.properties = testSuite.getElementsByTagName("properties").item(0);

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("could not read files in file tree", e);
        }


        //create merged test suite xml file
        Document doc = dBuilder.newDocument();
        Element testSuite = doc.createElement("testsuite");
        doc.appendChild(testSuite);
        testSuite.setAttribute("name", result.name);
        testSuite.setAttribute("errors", String.valueOf(result.errors));
        testSuite.setAttribute("failures", ((Integer) result.failures).toString());
        testSuite.setAttribute("tests", ((Integer) result.tests).toString());
        testSuite.setAttribute("skipped", ((Integer) result.skipped).toString());
        testSuite.setAttribute("time", ((Double) result.time).toString());
        //add properties
        Element propertiesElement = doc.createElement("properties");
        testSuite.appendChild(propertiesElement);
        for (int i = 0; i < result.properties.getChildNodes().getLength(); i++) {
            Node propertyOld = result.properties.getChildNodes().item(i);
            //ignore text content
            if (propertyOld.getNodeName().contentEquals("#text")) {
                continue;
            }

            Element property = doc.createElement("property");
            propertiesElement.appendChild(property);
            //set property attributes
            if (propertyOld.hasAttributes()) {
                for (int j = 0; j < propertyOld.getAttributes().getLength(); j++) {
                    Node attributeOld = propertyOld.getAttributes().item(j);
                    property.setAttribute(attributeOld.getNodeName(), attributeOld.getNodeValue());
                }

            }

        }

        //add test cases
        for (Node testCaseOld : result.testCases) {
            Element testCase = doc.createElement(testCaseOld.getNodeName());
            testSuite.appendChild(testCase);
            //set test case attributes
            for (int i = 0; i < testCaseOld.getAttributes().getLength(); i++) {
                Node attribute = testCaseOld.getAttributes().item(i);
                testCase.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
            }

            NodeList children = testCaseOld.getChildNodes();
            // set test ase child nodes
            for (int i = 0; i < children.getLength(); i++) {
                //ignore text content
                if (children.item(i).getNodeName().contentEquals("#text")) {
                    continue;
                }

                Element child = doc.createElement(children.item(i).getNodeName());
                testCase.appendChild(child);
                //set test case child node attributes
                for (int j = 0; j < children.item(i).getAttributes().getLength(); j++) {
                    Node attributeOld = children.item(i).getAttributes().item(j);
                    child.setAttribute(attributeOld.getNodeName(), attributeOld.getNodeValue());
                }

                child.setTextContent(children.item(i).getTextContent());
            }
        }

        createAggregatedTestSuite(doc);
    }

    private Document parseFile(Path file, DocumentBuilder dBuilder) throws IOException {
        try {
            return dBuilder.parse(file.toFile());
        } catch (SAXException e) {
            throw new RuntimeException("could not parse " + file, e);
        }
    }

    private DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            return dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e){
            throw new RuntimeException("Could not create a DOM parser", e);
        }
    }

    private void createAggregatedTestSuite(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(reportPath.resolve("TEST-Complete.TestSuite.xml").toFile());
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new RuntimeException("cannot create final TEST-Complete.TestSuite.xml", e);
        }
    }

    /**
     * // remove "Test-" from the file names
     */
    private void removePrefixOfTestResults(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //remove 'Test-' that this file is not detected by junit html report generation
                    if (file.getFileName().toString().startsWith("TEST-")) {
                        FileTasks.move(file, file.getParent().resolve(file.getFileName().toString().substring(5)));
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("could not rename the files in " + path, e);
        }
    }


}

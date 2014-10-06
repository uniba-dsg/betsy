package betsy.bpmn.reporting

import groovy.io.FileType
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.nio.file.Path

class BPMNTestcaseMerger {

    Path reportPath

    public void mergeTestCases(){
        //information needed for the generated test suite
        String name = ""
        int errors = 0
        int failures = 0
        int skipped = 0
        int tests = 0
        double time = 0.0
        List<Node> testCases = new ArrayList<>()
        Node properties = null

        //extract information
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance()
        File reportDir = new File(reportPath.toString())
        reportDir.eachFile(FileType.DIRECTORIES){ dir ->
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder()
            dir.eachFile(FileType.FILES) { file ->
                Document document = dBuilder.parse(file)
                document.getDocumentElement().normalize()
                Node testSuite = document.getDocumentElement()
                name = testSuite.getAttribute("name")
                errors += Integer.parseInt(testSuite.getAttribute("errors"))
                failures += Integer.parseInt(testSuite.getAttribute("failures"))
                skipped += Integer.parseInt(testSuite.getAttribute("skipped"))
                tests += Integer.parseInt(testSuite.getAttribute("tests"))
                time += Double.parseDouble(testSuite.getAttribute("time"))
                testCases.add(testSuite.getElementsByTagName("testcase").item(0))
                properties = testSuite.getElementsByTagName("properties").item(0)

                //remove 'Test-' that this file is not detected by junit html report generation
                file.renameTo(reportPath.resolve(dir.getName()).resolve(file.getName().substring(5)).toString())
            }
        }

        //create merged test suite xml file
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder()
        Document doc = dBuilder.newDocument()
        Element testSuite = doc.createElement("testsuite")
        doc.appendChild(testSuite)
        testSuite.setAttribute("name", name)
        testSuite.setAttribute("errors", errors.toString())
        testSuite.setAttribute("failures", failures.toString())
        testSuite.setAttribute("tests", tests.toString())
        testSuite.setAttribute("skipped", skipped.toString())
        testSuite.setAttribute("time", time.toString())
        //add properties
        Element propertiesElement = doc.createElement("properties")
        testSuite.appendChild(propertiesElement)
        for (int i = 0; i < properties.childNodes.length; i++){
            Node propertyOld = properties.childNodes.item(i)
            //ignore text content
            if(propertyOld.nodeName.contentEquals("#text")){
                continue
            }
            Element property = doc.createElement("property")
            propertiesElement.appendChild(property)
            //set property attributes
            if(propertyOld.hasAttributes()){
                for (int j = 0; j < propertyOld.attributes.length; j++){
                    Node attributeOld = propertyOld.attributes.item(j)
                    property.setAttribute(attributeOld.nodeName, attributeOld.nodeValue)
                }
            }
        }
        //add test cases
        for (Node testCaseOld : testCases){
            Element testCase = doc.createElement(testCaseOld.getNodeName())
            testSuite.appendChild(testCase)
            //set test case attributes
            for (int i = 0; i < testCaseOld.attributes.length; i++){
                Node attribute = testCaseOld.getAttributes().item(i)
                testCase.setAttribute(attribute.nodeName, attribute.nodeValue)
            }
            NodeList children = testCaseOld.childNodes
            // set test ase child nodes
            for(int i = 0; i < children.length; i++){
                //ignore text content
                if(children.item(i).getNodeName().contentEquals("#text")){
                    continue
                }
                Element child = doc.createElement(children.item(i).getNodeName())
                testCase.appendChild(child)
                //set test case child node attributes
                for (int j = 0; j < children.item(i).attributes.length; j++){
                    Node attributeOld = children.item(i).attributes.item(j)
                    child.setAttribute(attributeOld.nodeName, attributeOld.nodeValue)
                }
                child.setTextContent(children.item(i).textContent)
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance()
        Transformer transformer = transformerFactory.newTransformer()
        DOMSource source = new DOMSource(doc)
        StreamResult result = new StreamResult(new File("${reportPath}/TEST-Complete.TestSuite.xml"))
        transformer.transform(source, result)
    }
}

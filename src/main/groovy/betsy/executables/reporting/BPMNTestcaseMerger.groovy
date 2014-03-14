package betsy.executables.reporting

import groovy.io.FileType
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

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

        //extract information
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance()
        File reportDir = new File(reportPath.toString())
        reportDir.eachFile(FileType.FILES){ file ->
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder()
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

            //remove 'Test-' that this file is not detected by junit html report generation
            file.renameTo(reportPath.resolve(file.getName().substring(5)).toString())
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
        for (Node testCaseOld : testCases){
            Element testCase = doc.createElement(testCaseOld.getNodeName())
            testSuite.appendChild(testCase)
            for (int i = 0; i < testCaseOld.attributes.length; i++){
                Node attribute = testCaseOld.getAttributes().item(i)
                testCase.setAttribute(attribute.nodeName, attribute.nodeValue)
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance()
        Transformer transformer = transformerFactory.newTransformer()
        DOMSource source = new DOMSource(doc)
        StreamResult result = new StreamResult(new File("${reportPath}/TEST-Complete.TestSuite.xml"))
        transformer.transform(source, result)
    }
}

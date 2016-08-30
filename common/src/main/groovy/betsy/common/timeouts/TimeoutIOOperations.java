package betsy.common.timeouts;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutIOOperations {

    private static final Logger LOGGER = Logger.getLogger(TimeoutIOOperations.class);

    /**
     *
     * Proofs if some process failed.
     *
     * @param directory The directory of the result files.
     * @return Returns false, if one or more tests failed.
     */
    public static boolean testsAreCorrect(String directory) {
        String directoryName = Objects.requireNonNull(Objects.requireNonNull(directory, "The category can't be null."), "The testDirectory can't be null.") + "/reports";
        List<Path> files = FileTasks.findAllInFolder(Paths.get(directoryName), "TESTS-TestSuites.xml");
        for (Path path : files) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(path.toFile());
                document.getDocumentElement();
                NodeList testCases = document.getElementsByTagName("testsuite");

                int countFailures = 0;
                for (int i = 0; i < testCases.getLength(); i++) {
                    Node testcase = testCases.item(i);
                    NamedNodeMap attributes = testcase.getAttributes();
                    Node failures = attributes.getNamedItem("failures");
                    int value = Integer.parseInt(failures.getNodeValue());
                    countFailures = countFailures + value;
                }
                if(countFailures > 0){
                    return false;
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the file: " + path.getFileName());
            } catch (ParserConfigurationException | SAXException e) {
                LOGGER.info("Couldn't parse the file: " + path.getFileName());
            }
        }
        return true;

    }

}


package betsy.common.timeouts;

import betsy.common.tasks.FileTasks;
import betsy.common.timeouts.calibration.CalibrationTimeout;
import betsy.common.timeouts.calibration.CalibrationTimeoutRepository;
import betsy.common.timeouts.timeout.Timeout;
import betsy.common.timeouts.timeout.TimeoutRepository;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ExternalTimeouts {

    private static final Logger LOGGER = Logger.getLogger(ExternalTimeouts.class);


    /**
     * This method reads the timeouts from the SOAPUI result xml.
     *
     * @param testDirectory The directory, which contains the SOAPUI test files.
     */
    public static void readSoapUITimeouts(String testDirectory) {

        String directoryName = testDirectory + "/reports";
        List<Path> files = FileTasks.findAllInFolder(Paths.get(directoryName), "TESTS-TestSuites.xml");

        for (Path path : files) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(path.toFile());
                document.getDocumentElement();
                NodeList testCases = document.getElementsByTagName("testcase");

                for (int i = 0; i < testCases.getLength(); i++) {
                    Node testcase = testCases.item(i);
                    Optional<Node> child = Optional.ofNullable(testcase.getFirstChild());
                    Optional<Node> failure = Optional.empty();
                    if (child.isPresent()) {
                        failure = Optional.ofNullable(child.get().getNextSibling());
                    }
                    if (failure.isPresent() && failure.get().getNodeName().equals("failure")) {
                        CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout("TestingAPI", "constructor", TimeoutRepository.getTimeout("TestingAPI.constructor").get().getTimeoutInMs()));
                        CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
                    } else {
                        Optional<Node> attribute = Optional.ofNullable(testcase.getAttributes().getNamedItem("time"));
                        if (attribute.isPresent()) {
                            Integer value = Integer.parseInt(attribute.get().getNodeValue().replace(".", ""));
                            CalibrationTimeout calibrationTimeout = new CalibrationTimeout(new Timeout("TestingAPI", "constructor", value));
                            CalibrationTimeoutRepository.addCalibrationTimeout(calibrationTimeout);
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.info("Couldn't read the file: " + path.getFileName());
            } catch (ParserConfigurationException | SAXException e) {
                LOGGER.info("Couldn't parse the file: " + path.getFileName());
            }
        }

    }
}


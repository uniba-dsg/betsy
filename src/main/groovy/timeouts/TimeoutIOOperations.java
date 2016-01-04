package timeouts;

import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import timeouts.calibration_timeout.CalibrationTimeout;
import timeouts.calibration_timeout.CalibrationTimeoutRepository;
import timeouts.timeout.Timeout;
import timeouts.timeout.TimeoutRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TimeoutIOOperations {

    private static final Logger LOGGER = Logger.getLogger(TimeoutIOOperations.class);

    /**
     * This method reads the values of the given timeouts form the properties.
     *
     * @param propertiesFile The file of the properties.
     * @param timeouts       The list with the timeouts.
     * @return A {@link List} with the timeouts and the loaded values from the properties.
     */
    public static List<Timeout> readFromProperties(File propertiesFile, List<Timeout> timeouts) {
        if (propertiesFile != null && timeouts != null) {
            if (Files.isReadable(propertiesFile.toPath())) {
                Reader reader = null;
                try {
                    reader = new FileReader(propertiesFile);
                    Properties properties = new Properties();
                    properties.load(reader);

                    for (Timeout timeout : timeouts) {
                        String value = properties.getProperty(timeout.getKey() + ".value");
                        if (value != null) {
                            try {
                                timeout.setValue(new Integer(value));
                            } catch (NumberFormatException e) {
                                LOGGER.info("The timeout with the key " + timeout.getKey() + " was not read from the properties. The timeout have to be an integer.");
                            }
                        }

                        String timeToRepetition = properties.getProperty(timeout.getKey() + ".timeToRepetition");
                        if (timeToRepetition != null) {
                            try {
                                timeout.setTimeToRepetition(new Integer(timeToRepetition));
                            } catch (NumberFormatException e) {
                                LOGGER.info("The timeToRepetition with the key " + timeout.getKey() + " was not read from the properties. The timeToRepetition have to be an integer.");
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.info("Couldn't read the {@link Timeout} properties.");
                } finally {
                    assert reader != null;
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOGGER.info("Couldn't close the {@link Timeout} properties {@Link FileReader}.");
                    }
                }
            } else {
                LOGGER.info("The file " + propertiesFile.getName() + " is not readable.");
            }
        } else {
            LOGGER.info("The property file or the timeouts were null.");
        }
        return timeouts;
    }

    /**
     * This method writes the values of the given timeouts to the property file.
     *
     * @param propertiesFile The file were the properties should be saved.
     * @param timeouts       The timeouts, which should be saved.
     */
    public static void writeToProperties(File propertiesFile, List<Timeout> timeouts) {
        if (propertiesFile != null && timeouts != null) {
            Writer writer = null;
            try {
                Properties properties;
                if (Files.isReadable(propertiesFile.toPath())) {
                    Reader reader = new FileReader(propertiesFile);
                    properties = new Properties();
                    properties.load(reader);
                    reader.close();
                } else {
                    properties = new Properties();
                }
                writer = new FileWriter(propertiesFile);
                for (Timeout timeout : timeouts) {
                    properties.setProperty(timeout.getKey() + ".value", Integer.toString(timeout.getTimeoutInMs()));
                    properties.setProperty(timeout.getKey() + ".timeToRepetition", Integer.toString(timeout.getTimeToRepetitionInMs()));
                }
                properties.store(writer, "Timeout_properties");
                writer.close();
            } catch (IOException e) {
                LOGGER.info("Couldn't store the properties ");
            } finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (Exception e) {
                    LOGGER.info("Couldn't close the {@link Timeout} properties {@Link FileReader} ");
                }
            }
        } else {
            LOGGER.info("The property file or the timeouts were null.");
        }
    }

    /**
     * This method writes the values of timeouts to a csv file.
     *
     * @param csv      The csv file, where the timeout values should be saved.
     * @param timeouts The timeouts to save.
     */
    public static void writeToCSV(File csv, List<Timeout> timeouts) {
        if (csv != null && timeouts != null) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(csv);
                writer.append("Key").append(';');
                writer.append("EngineOrProcessGroup").append(';');
                writer.append("StepOrProcess").append(';');
                writer.append("Value").append(';');
                writer.append("TimeToRepetition").append('\n');
                for (Timeout timeout : timeouts) {
                    writer.append(timeout.getKey()).append(';');
                    writer.append(timeout.getEngineOrProcessGroup()).append(';');
                    writer.append(timeout.getStepOrProcess()).append(';');
                    writer.append(Integer.toString(timeout.getTimeoutInMs())).append(';');
                    writer.append(Integer.toString(timeout.getTimeToRepetitionInMs())).append('\n');
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                LOGGER.info("Could not write the timeouts to csv file.");
            } finally {
                try {
                    assert writer != null;
                    writer.close();
                } catch (Exception e) {
                    LOGGER.info("Couldn't close the csv file.");
                }
            }
        } else {
            LOGGER.info("The csv file or the timeouts were null.");
        }
    }


    /**
     * This method reads the timeouts from the SOAPUI result xml.
     *
     * @param testDirectory The directory, which contains the SOAPUI test files.
     * @return This method returns a list of CalibrationTimeouts.
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
                    Optional<Node> failure = Optional.ofNullable(testcase.getFirstChild().getNextSibling());

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

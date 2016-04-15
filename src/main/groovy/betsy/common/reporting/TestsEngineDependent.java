package betsy.common.reporting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestsEngineDependent {

    private static final String FILE_NAME = "tests-engine-dependent.json";

    private final BPMNTestSuite processes;

    public TestsEngineDependent(BPMNTestSuite processes) {
        this.processes = processes;
    }

    public void createJson() {
        Path outputPath = getOutputPath();

        JSONArray rootArray = new JSONArray();

        // TODO also write other jsons to test folder

        String currentGitCommit = getGitCommit();
        Map<String, Long> idToDuration = readDurations(processes.getCsvDurationFilePath());
        List<CsvRow> csvRows = new JUnitXmlResultReader(processes.getJUnitXMLFilePath()).readRows();

        for (AbstractBPMNEngine engine : processes.getEngines()) {
            for (BPMNProcess process : engine.getProcesses()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("featureID", process.getFeature().getID());
                jsonObject.put("engineID", process.getEngine().getEngineId().getID());

                JSONObject toolObject = new JSONObject();
                toolObject.put("name", "betsy");
                toolObject.put("version", currentGitCommit);
                toolObject.put("toolID", String.join("__", "betsy", currentGitCommit));
                jsonObject.put("tool", toolObject);

                List<String> logFiles = new LinkedList<>();
                try {
                    logFiles.addAll(Files.list(process.getTargetLogsPath())
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jsonObject.put("logFiles", new JSONArray(logFiles));

                List<String> engineDependentFiles = new LinkedList<>();
                try {
                    engineDependentFiles.addAll(Files.list(process.getTargetProcessPath())
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(Files.exists(process.getTargetPackagePath())) {
                    try {
                        engineDependentFiles.addAll(Files.list(process.getTargetPackagePath())
                                .map(Object::toString)
                                .collect(Collectors.toList()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                jsonObject.put("engineDependentFiles", new JSONArray(logFiles));

                long executionTime = -1;
                try {
                    executionTime = Files.readAttributes(process.getTargetPath(), BasicFileAttributes.class).creationTime().toMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                jsonObject.put("executionTimestamp", executionTime);

                String groupFeatureID = process.getFeatureDimension().getGroupFeatureID();
                String localID = process.getEngine().getEngineId().getID() + "__" + groupFeatureID;
                long duration = idToDuration.get(localID);
                jsonObject.put("executionDuration", duration);

                // parse JUnit XML for results
                JSONObject resultObject = new JSONObject();
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    resultObject.put("testCases", csvRow.getTests());
                    resultObject.put("testCaseFailures", csvRow.getFailures());
                    resultObject.put("testCaseSuccesses", csvRow.getTests() - csvRow.getFailures());
                    resultObject.put("testSuccessful", csvRow.isSuccess());
                    resultObject.put("testDeployable", csvRow.isDeployable());
                    resultObject.put("testResult", csvRow.getFailures() == 0 ? "+" : csvRow.getSuccesses() == 0 ? "-" : "+/-");
                });
                jsonObject.put("result", resultObject);

                // parse JUnit XML
                JSONArray testCases = new JSONArray();
                try {
                    testCases = parseTestCases(process.getTargetReportsPathJUnitXml());
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                }
                jsonObject.put("testCases", testCases);

                rootArray.put(jsonObject);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(getOutputPath(), StandardOpenOption.CREATE)) {
            writer.append(rootArray.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getOutputPath() {
        return processes.getPath().resolve(FILE_NAME);
    }

    private JSONArray parseTestCases(Path reportTestCasePath)
            throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(reportTestCasePath.toFile());
        NodeList testCaseNodes = document.getElementsByTagName("testcase");

        JSONArray testCases = new JSONArray();

        for (int i = 0; i < testCaseNodes.getLength(); i++) {
            Node tcNode = testCaseNodes.item(i);
            NamedNodeMap attr = tcNode.getAttributes();

            JSONObject testCaseObject = new JSONObject();
            testCaseObject.put("name", attr.getNamedItem("name").getNodeValue());
            testCaseObject.put("number", i + 1);
            testCaseObject.put("message", tcNode.getTextContent());
            testCases.put(testCaseObject);
        }

        return testCases;
    }

    private Map<String, Long> readDurations(Path durationsCsv) {
        Map<String, Long> idToDuration = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(durationsCsv)) {
            String line = br.readLine();
            while (line != null) {
                String[] tokens = line.split(";");
                if (tokens.length == 4) {
                    long duration = Long.parseLong(tokens[0]);
                    String engineTestId = tokens[2] + "__" + tokens[3];
                    System.out.println("Reading duration for test: " + engineTestId);
                    idToDuration.put(engineTestId, duration);
                }
                line = br.readLine();
            }
        } catch (IOException ioe) {
        }
        return idToDuration;
    }

    private String getGitCommit() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec("git rev-parse HEAD");

            try (BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
                String line = input.readLine();
                System.out.println("GIT COMMIT: " + line);
                if (line.startsWith("fatal:")) {
                    //no commit hash
                    return "";
                } else {
                    return line;
                }
            }
        } catch (IOException e) {
            return "";
        }
    }
}

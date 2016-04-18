package betsy.common.reporting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
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
import betsy.common.util.DurationCsv;
import betsy.common.util.GitUil;
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

        String currentGitCommit = GitUil.getGitCommit();
        Map<String, Long> idToDuration = DurationCsv.readDurations(processes.getCsvDurationFilePath());
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
                }
                jsonObject.put("logFiles", new JSONArray(logFiles));

                List<String> engineDependentFiles = new LinkedList<>();
                try {
                    engineDependentFiles.addAll(Files.list(process.getTargetProcessPath())
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                } catch (IOException e) {
                }

                if (Files.exists(process.getTargetPackagePath())) {
                    try {
                        engineDependentFiles.addAll(Files.list(process.getTargetPackagePath())
                                .map(Object::toString)
                                .collect(Collectors.toList()));
                    } catch (IOException e) {
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

                String groupFeatureID = process.getGroupFeatureID();
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
                JSONArray testCases = parseTestCases(process.getTargetReportsPathJUnitXml());
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

    private JSONArray parseTestCases(Path reportTestCasePath) {
        try {
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
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return new JSONArray();
        }
    }

}

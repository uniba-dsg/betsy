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

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.model.BPELTestSuite;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.model.BPMNProcess;
import betsy.bpmn.model.BPMNTestSuite;
import betsy.common.model.ProcessFolderStructure;
import betsy.common.model.engine.EngineDimension;
import betsy.common.model.feature.FeatureDimension;
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

    public void createJson(BPMNTestSuite testSuite) {
        JSONArray rootArray = new JSONArray();

        // TODO also write other jsons to test folder

        String currentGitCommit = GitUil.getGitCommit();
        Map<String, Long> idToDuration = DurationCsv.readDurations(testSuite.getCsvDurationFilePath());
        List<CsvRow> csvRows = new JUnitXmlResultReader(testSuite.getJUnitXMLFilePath()).readRows();

        for (AbstractBPMNEngine engine : testSuite.getEngines()) {
            for (BPMNProcess process : engine.getProcesses()) {
                rootArray.put(createJsonObject(currentGitCommit, idToDuration, csvRows, process));
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(testSuite.getPath().resolve(FILE_NAME), StandardOpenOption.CREATE)) {
            writer.append(rootArray.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createJson(BPELTestSuite testSuite) {
        JSONArray rootArray = new JSONArray();

        // TODO also write other jsons to test folder

        String currentGitCommit = GitUil.getGitCommit();
        Map<String, Long> idToDuration = DurationCsv.readDurations(testSuite.getCsvDurationFilePath());
        List<CsvRow> csvRows = new JUnitXmlResultReader(testSuite.getJUnitXMLFilePath()).readRows();

        for (AbstractBPELEngine engine : testSuite.getEngines()) {
            for (BPELProcess process : engine.getProcesses()) {
                rootArray.put(createJsonObject(currentGitCommit, idToDuration, csvRows, process));
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(testSuite.getPath().resolve(FILE_NAME), StandardOpenOption.CREATE)) {
            writer.append(rootArray.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <P extends FeatureDimension & ProcessFolderStructure & EngineDimension> JSONObject createJsonObject(String currentGitCommit, Map<String, Long> idToDuration, List<CsvRow> csvRows, P process) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("featureID", process.getFeature().getID());
        jsonObject.put("engineID", process.getEngineObject().getID());

        jsonObject.put("tool", createToolJsonObject(currentGitCommit));

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
        jsonObject.put("engineDependentFiles", new JSONArray(engineDependentFiles));

        long executionTime = -1;
        try {
            executionTime = Files.readAttributes(process.getTargetPath(), BasicFileAttributes.class).creationTime().toMillis();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject.put("executionTimestamp", executionTime);

        String groupFeatureID = process.getGroupFeatureID();
        String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
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
        return jsonObject;
    }

    private static JSONObject createToolJsonObject(String currentGitCommit) {
        JSONObject toolObject = new JSONObject();
        toolObject.put("name", "betsy");
        toolObject.put("version", currentGitCommit);
        toolObject.put("toolID", String.join("__", "betsy", currentGitCommit));
        return toolObject;
    }

    private static JSONArray parseTestCases(Path reportTestCasePath) {
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

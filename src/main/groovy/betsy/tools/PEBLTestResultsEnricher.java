package betsy.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import betsy.common.reporting.CsvRow;
import betsy.common.reporting.JUnitXmlResultReader;
import betsy.common.util.DurationCsv;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pebl.benchmark.feature.FeatureDimension;
import pebl.benchmark.feature.Metric;
import pebl.benchmark.test.Test;
import pebl.result.Measurement;
import pebl.result.engine.Engine;
import pebl.result.metrics.BooleanValue;
import pebl.result.metrics.LongValue;
import pebl.result.metrics.StringValue;
import pebl.result.test.TestCaseResult;
import pebl.result.test.TestResult;
import pebl.result.tool.Tool;
import pebl.xsd.PEBL;

public class PEBLTestResultsEnricher {

    private static final String FILE_NAME = "tests-engine-dependent.json";

    public void addTestResults(BPMNTestSuite testSuite, PEBL pebl) {
        Map<String, Long> idToDuration = DurationCsv.readDurations(testSuite.getCsvDurationFilePath());
        List<CsvRow> csvRows = new JUnitXmlResultReader(testSuite.getJUnitXMLFilePath()).readRows();

        for (AbstractBPMNEngine engine : testSuite.getEngines()) {
            for (BPMNProcess process : engine.getProcesses()) {
                pebl.result.testResults.add(createTestResult(idToDuration, csvRows, process, pebl));
            }
        }
    }

    public void addTestResults(BPELTestSuite testSuite, PEBL pebl) {
        Map<String, Long> idToDuration = DurationCsv.readDurations(testSuite.getCsvDurationFilePath());
        List<CsvRow> csvRows = new JUnitXmlResultReader(testSuite.getJUnitXMLFilePath()).readRows();

        for (AbstractBPELEngine engine : testSuite.getEngines()) {
            for (BPELProcess process : engine.getProcesses()) {
                pebl.result.testResults.add(createTestResult(idToDuration, csvRows, process, pebl));
            }
        }
    }

    private static <P extends FeatureDimension & ProcessFolderStructure & EngineDimension> TestResult createTestResult(Map<String, Long> idToDuration,
            List<CsvRow> csvRows,
            P process,
            PEBL pebl) {
        Tool tool = pebl.result.tools.get(0);
        Engine engine = pebl.result.engines.stream().filter(e -> e.getID().equals(process.getEngineObject().getID())).findFirst().orElseThrow(() -> new IllegalStateException("could not find engine " + process.getEngineObject().getID()));
        Test test = pebl.benchmark.tests.stream().filter(t -> t.getID().equals(process.getFeatureID() + "__" + "test")).findFirst().orElseThrow(() -> new IllegalStateException("could not find test " + process.getFeatureID() + "__" + "test"));

        List<Path> logFiles = new LinkedList<>();
        try (Stream<Path> list = Files.list(process.getTargetLogsPath())) {
            logFiles.addAll(list
                    .collect(Collectors.toList()));
        } catch (IOException e) {
        }

        List<Path> files = new LinkedList<>();
        try (Stream<Path> list = Files.list(process.getTargetProcessPath())) {
            files.addAll(list
                    .collect(Collectors.toList()));
        } catch (IOException e) {
        }

        if (Files.exists(process.getTargetPackagePath())) {
            try (Stream<Path> list = Files.list(process.getTargetPackagePath())) {
                files.addAll(list.collect(Collectors.toList()));
            } catch (IOException e) {
            }
        }

        List<Measurement> measurements = new LinkedList<>();
        for (Metric metric : test.getMetrics()) {
            if (metric.getName().equals("executionTimestamp")) {
                long executionTime = -1;
                try {
                    executionTime = Files.readAttributes(process.getTargetPath(), BasicFileAttributes.class).creationTime().toMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                measurements.add(new Measurement(metric, new LongValue(executionTime)));
            } else if (metric.getName().equals("executionDuration")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                long duration = idToDuration.get(localID);

                measurements.add(new Measurement(metric, new LongValue(duration)));
            } else if (metric.getName().equals("testCases")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new LongValue(csvRow.getTests())));
                });
            } else if (metric.getName().equals("testCaseFailures")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new LongValue(csvRow.getFailures())));
                });
            } else if (metric.getName().equals("testCaseSuccesses")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new LongValue(csvRow.getTests() - csvRow.getFailures())));
                });
            } else if (metric.getName().equals("testSuccessful")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new BooleanValue(csvRow.isSuccess())));
                });
            } else if (metric.getName().equals("testDeployable")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new BooleanValue(csvRow.isDeployable())));
                });
            } else if (metric.getName().equals("testResult")) {
                String groupFeatureID = process.getGroupFeatureID();
                String localID = process.getEngineObject().getID() + "__" + groupFeatureID;
                csvRows.stream().filter(row -> localID.equals(String.join("__", row.getEngine(), row.getGroup(), row.getName()))).findFirst().ifPresent(csvRow -> {
                    measurements.add(new Measurement(metric, new StringValue(csvRow.getFailures() == 0 ? "+" : csvRow.getSuccesses() == 0 ? "-" : "+/-")));
                });
            }
        }

        // parse JUnit XML
        List<TestCaseResult> testCaseResults = parseTestCases(process.getTargetReportsPathJUnitXml());
        return new TestResult(test, engine, tool, logFiles, process.getTargetPackagePath(), files, measurements, Collections.emptyMap(), testCaseResults);
    }

    private static List<TestCaseResult> parseTestCases(Path reportTestCasePath) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(reportTestCasePath.toFile());
            NodeList testCaseNodes = document.getElementsByTagName("testcase");

            JSONArray testCases = new JSONArray();

            List<TestCaseResult> testCaseResults = new LinkedList<>();

            for (int i = 0; i < testCaseNodes.getLength(); i++) {
                Node tcNode = testCaseNodes.item(i);
                NamedNodeMap attr = tcNode.getAttributes();
                TestCaseResult testCaseResult = new TestCaseResult(attr.getNamedItem("name").getNodeValue(), i + 1, tcNode.getTextContent());
                testCaseResults.add(testCaseResult);
            }

            return testCaseResults;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return new LinkedList<>();
        }
    }

}

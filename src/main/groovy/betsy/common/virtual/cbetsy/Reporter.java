package betsy.common.virtual.cbetsy;

import betsy.common.tasks.FileTasks;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * With this class contains methods to create reports.
 *
 * @author Christoph Broeker
 * @version 1.0
 */
public class Reporter {

    private static final Logger LOGGER = Logger.getLogger(Reporter.class);
    private static Path docker = Paths.get(get("docker.dir")).toAbsolutePath();

    /**
     * This method creates a reports for the given workerTemplates.
     *
     * @param workerTemplateGenerator The workerTemplates for creating the results.
     * @param build                   The duration of the build process.
     * @param resource                The duration of  the resource configuration.
     * @param timeout                 The duration of the timeout calibration.
     * @param execution               The duration of the test execution.
     */
    public static void createReport(WorkerTemplateGenerator workerTemplateGenerator, long build, long resource, long timeout, long execution) {
        HashMap<String, Boolean> results = new HashMap<>();
        ArrayList<String> paths = new ArrayList<>();
        scanForFiles(Paths.get("results"), "results.csv", paths).forEach(e -> {
            String[] value = readFile(new File(e));
            if (value[1].equals("1")) {
                results.put(value[0], true);
            } else {
                results.put(value[0], false);
            }
        });
        Path htmlFile = docker.resolve("results").resolve("result.html");
        Path resultFile = Paths.get("results").resolve("result.html").toAbsolutePath();
        FileTasks.copyFileContentsToNewFile(htmlFile, resultFile);
        Map<String, Object> replacements = new HashMap<>();
        replacements.put("@BUILD@", build / 1000);
        replacements.put("@RESOURCE@", resource / 1000);
        replacements.put("@TIMEOUT@", timeout / 1000);
        replacements.put("@EXECUTION@", execution / 1000);
        String[] bpel = createTable(workerTemplateGenerator.getBPELEngines(), workerTemplateGenerator.getBPELProcesses(), results);
        replacements.put("@BPEL@", bpel[0]);
        replacements.put("@BPELSUCCESSFUL@", bpel[1]);
        replacements.put("@BPELFAILED@", bpel[2]);
        replacements.put("@BPELMISSING@", bpel[3]);
        replacements.put("@BPELTOTAL@", bpel[4]);
        String[] bpmn = createTable(workerTemplateGenerator.getBPMNEngines(), workerTemplateGenerator.getBPMNProcesses(), results);
        replacements.put("@BPMN@", bpmn[0]);
        replacements.put("@BPMNSUCCESSFUL@", bpmn[1]);
        replacements.put("@BPMNFAILED@", bpmn[2]);
        replacements.put("@BPMNMISSING@", bpmn[3]);
        replacements.put("@BPMNTOTAL@", bpmn[4]);
        FileTasks.replaceTokensInFile(resultFile, replacements);
    }


    private static List<String> scanForFiles(Path path, String fileName, ArrayList<String> paths) {
        File[] files = path.toFile().listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanForFiles(Paths.get(file.toURI()), fileName, paths);
                } else if (file.getName().equals(fileName)) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return paths;
    }

    private static String[] readFile(File file) {
        String[] values = new String[0];
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                values = line.split(";");
            }
        } catch (IOException x) {
            LOGGER.info("Can' read file.");
        }
        return new String[]{values[0] + "_" + values[1], values[3]};
    }

    private static String[] createTable(HashSet<DockerEngine> engines, ArrayList<String> processes, HashMap<String, Boolean> values) {
        int total = 0;
        int missing = 0;
        int successful = 0;
        int failure = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("<table class=\"table table-hover\">\n");

        //Create header
        builder.append("<thead><th>Engines:</td>");
        engines.forEach(e -> builder.append("<th>" + e.getName() + "</th>"));
        builder.append("</thead>\n").append("<tbody>\n");

        //Create table
        for (String process : processes) {
            builder.append("<tr><td>").append(process).append("</td>");
            for (DockerEngine engine : engines) {
                total++;
                String key = process + "_" + engine.getName();
                Optional<Boolean> value = Optional.ofNullable(values.get(key));
                key = engine.getName().replace("_", "") + process.replace("_", "");


                if (value.isPresent()) {
                    if (value.get()) {
                        successful++;
                        builder.append("<td><span class=\"label label-success glyphicon glyphicon-ok\"><a href=\"").append(key).append("/test/reports/results.html\" style=\"color: #FFFFFF\"> Success</a></span></td>");
                    } else {
                        failure++;
                        builder.append("<td><span class=\"label label-danger glyphicon glyphicon-remove\"><a href=\"").append(key).append("/test/reports/results.html\" style=\"color: #FFFFFF\"> Failure</a></span></td>");
                    }
                } else {
                    missing++;
                    builder.append("<td><span class=\"label label-warning glyphicon glyphicon-search\">").append(" Missing</span></td>");
                }
            }
            builder.append("</tr>\n");
        }
        builder.append("</tbody>\n");
        builder.append("<table class=\"table table-bordered\">\n\n");
        return new String[]{builder.toString(), String.valueOf(successful), String.valueOf(failure), String.valueOf(missing), String.valueOf(total)};
    }
}


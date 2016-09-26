package betsy.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import betsy.bpmn.BPMNMain;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.repositories.BPMNEngineRepository;
import betsy.common.analytics.CsvReportLoader;
import betsy.common.analytics.html.HtmlAnalytics;
import betsy.common.analytics.model.CsvReport;
import betsy.common.tasks.WaitTasks;
import betsy.common.tasks.ZipTasks;

/**
 * Tool to batch execute tests without reinstall/restart of engines between each test
 */
public class BatchTestRunningEngines {

    public static void main(String[] args) throws IOException {
        String enginesString = args[0];
        String testsString = args[1];

        long timestamp = System.currentTimeMillis();

        Path globalResultFile = Paths.get("test-zips/"+timestamp+"_globalResults.csv");
        Files.createFile(globalResultFile);

        BPMNEngineRepository bpmnEngineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = bpmnEngineRepository.getByNames(enginesString.split(","));

        for(AbstractBPMNEngine engine : engines) {
            if(engine.isInstalled()) {
                engine.uninstall();
            }

            engine.install();

            engine.startup();

            BPMNMain.main(engine.getEngineID(), testsString, "-k", "-r");

            engine.shutdown();

            appendResultsToGlobalFile(globalResultFile, Paths.get("test/reports/results.csv"));

            WaitTasks.sleep(5000);

            ZipTasks.zipFolder(Paths.get("test-zips/"+engine.getEngineID()+"_"+timestamp+"-test.zip"),Paths.get("test"));

        }

        new HtmlAnalytics(new CsvReportLoader(globalResultFile, new CsvReport()).load()).toHtmlReport(Paths.get("test-zips/"+timestamp+"_globalReport.html"));
    }

    private static void appendResultsToGlobalFile(Path target, Path toAdd) throws IOException {
        Files.write(target, Files.readAllLines(toAdd, StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }
}

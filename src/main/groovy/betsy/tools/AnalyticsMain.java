package betsy.tools;

import betsy.common.analytics.Analyzer;
import betsy.common.analytics.model.CsvReport;
import configuration.bpel.BPELProcessRepository;
import configuration.bpmn.BPMNProcessRepository;

import java.nio.file.Paths;

public class AnalyticsMain {

    public static void main(String... args) {
        if ("dashboard".equalsIgnoreCase(args[0])) {
            dashboard(args[1], args[2]);
        } else {
            System.out.println("dashboard CSV_FILE");
        }
    }

    private static void dashboard(String csvFile, String folder) {
        new Analyzer(Paths.get(csvFile), Paths.get(folder)).createAnalytics(new CsvReport());
    }

}

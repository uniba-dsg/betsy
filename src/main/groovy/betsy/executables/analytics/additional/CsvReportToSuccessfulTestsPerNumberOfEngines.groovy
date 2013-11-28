package betsy.executables.analytics.additional

import betsy.executables.analytics.CsvReportLoader
import betsy.executables.analytics.model.CsvReport
import betsy.executables.analytics.model.Result

import java.nio.file.Paths

class CsvReportToSuccessfulTestsPerNumberOfEngines {

    static class ResultPair {
        String name
        int successful
    }

    CsvReport report

    void toCsvReport(PrintStream writer) {
        int total = report.tests.size()


        List<ResultPair> results = []
        writer.println "Raw"
        report.tests.each { test ->
            int successful = test.engineToResult.values().count { it.partial == Result.Support.TOTAL}
            results << new ResultPair(name: test.name, successful: successful)
        }

        results.sort { a,b -> (b.successful <=> a.successful) ?: (a.name <=> b.name)}

        results.each { result ->
            writer.println "${result.name}\t${result.successful}"
        }

        writer.println "Aggregated"
        results.groupBy { it.successful }.sort() {it.key}.each { key, values ->
            writer.println key + "\t" + values.size() + "\t" + (int)Math.round((double) values.size() / total * 100) + "%"
        }

        writer.println "At least one engine"
        writer.println results.findAll(){it.successful > 0}.size() + "\t" +  (int)Math.round((double) results.findAll(){it.successful > 0}.size() / total * 100) + "%"
    }

    public static void main(String[] args) {
        new CsvReportToSuccessfulTestsPerNumberOfEngines(report: new CsvReportLoader(csvFile: Paths.get(args[0])).load()).toCsvReport(System.out)
    }
}

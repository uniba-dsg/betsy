package betsy.common.analytics.additional

import betsy.common.analytics.aggregation.TrivalentResult
import betsy.common.analytics.CsvReportLoader
import betsy.common.analytics.model.CsvReport

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
            int successful = test.engineToResult.values().count { it.support == TrivalentResult.PLUS}
            results << new ResultPair(name: test.name, successful: successful)
        }

        results.sort { a,b -> (b.successful <=> a.successful) ?: (a.name <=> b.name)}

        results.each { result ->
            writer.println "${result.name}\t${result.successful}"
        }

        writer.println "Aggregated"
        results.groupBy { it.successful }.sort() {it.key}.each { key, values ->
            int i = (int) Math.round((double) values.size() / total * 100)
            writer.println key + "\t" + values.size() + "\t" + i  + "%"
        }

        writer.println "At least one engine"
        int i = (int)Math.round((double) results.findAll(){it.successful > 0}.size() / total * 100);
        writer.println results.findAll(){it.successful > 0}.size() + "\t" +  i + "%"
    }

    public static void main(String[] args) {
        new CsvReportToSuccessfulTestsPerNumberOfEngines(report: new CsvReportLoader(Paths.get(args[0]), new CsvReport()).load()).toCsvReport(System.out)
    }
}

package betsy.common.analytics.additional

import betsy.common.aggregation.TrivalentResult
import betsy.common.analytics.CsvReportLoader
import betsy.common.analytics.model.CsvReport

import java.nio.file.Paths


class CsvReportToPortabilityData {

    CsvReport report

    static class EngineResults {
        int[] results
        String name

        public int count() {
            int result = 0;
            for (int i = 0; i < results.length; i++) {
                result += results[i]
            }
            return result;
        }

        @Override
        public String toString() {
            return "EngineResults{" +
                    ", name='" + name + '\'' +
                    ", count='" + count() + '\'' +
                    '}';
        }
    }

    static class Portability {
        List<String> engines
        int commonSuccessfulTests

        @Override
        public String toString() {
            return "Portability{" +
                    "engines=" + engines +
                    ", commonSuccessfulTests=" + commonSuccessfulTests +
                    '}';
        }
    }

    void toCsvReport(PrintStream writer) {
        int totalTests = report.getTests().size()

        def arrays = report.engines.collect { engine ->
            new EngineResults(name: engine.name, results: report.getTests().sort {it.name}.collect { test ->
                test.engineToResult.get(engine).support == TrivalentResult.PLUS ? 1 : 0
            }
            )
        }

        arrays.each {result ->
            writer.println result
        }

        List<Portability> portabilities = []
        arrays.subsequences().findAll { it.size() > 1}.each { subsequence ->
            portabilities << new Portability(engines: subsequence.collect {it.name}.sort(),
                    commonSuccessfulTests: count(xor(subsequence.collect {it.results}))
            )
        }

        portabilities.sort {a, b -> b.commonSuccessfulTests <=> a.commonSuccessfulTests}

        def portBetweenTwo = portabilities.findAll {it.engines.size() == 2}
        writer.println "Comparing portability between 2 engines"
        List<String> engines = report.engines.collect() {it.name}.sort()
        writer.println "\t" + engines.join("\t")
        engines.each { a ->
            writer.print a + "\t"
            engines.each { b ->
                if (a == b) {
                    writer.print "-"
                } else {
                    writer.print "" + portBetweenTwo.find { it.engines.sort().containsAll([a, b])}.commonSuccessfulTests
                }
                writer.print "\t"
            }
            writer.print "\n"
        }
        writer.println "Now in percentage"
        writer.println "\t" + engines.join("\t")
        engines.each { a ->
            writer.print a + "\t"
            engines.each { b ->
                if (a == b) {
                    writer.print "-"
                } else {
                    double v = (double) portBetweenTwo.find { it.engines.sort().containsAll([a, b])}.commonSuccessfulTests
                    writer.print ((int)Math.round((v / totalTests) * 100))
                    writer.print "%"
                }
                writer.print "\t"
            }
            writer.print "\n"
        }

        def portBetweenThree = portabilities.findAll {it.engines.size() == 3}
        writer.println "Comparing portability between 3 engines"
        portBetweenThree.each { p ->
            writer.print p.commonSuccessfulTests + "/$totalTests ${(int)Math.round((double) p.commonSuccessfulTests / totalTests * 100)}%"
            writer.print "\t"
            writer.print p.engines
            writer.print "\n"
        }

        def portBetweenFour = portabilities.findAll {it.engines.size() == 4}
        writer.println "Comparing portability between 4 engines"
        portBetweenFour.each { p ->
            writer.print p.commonSuccessfulTests + "/$totalTests ${(int)Math.round((double) p.commonSuccessfulTests / totalTests * 100)}%"
            writer.print "\t"
            writer.print p.engines
            writer.print "\n"
        }

        def portBetweenFive = portabilities.findAll {it.engines.size() == 5}
        writer.println "Comparing portability between 5 engines"
        portBetweenFive.each { p ->
            writer.print p.commonSuccessfulTests + "/$totalTests ${(int)Math.round((double) p.commonSuccessfulTests / totalTests * 100)}%"
            writer.print "\t"
            writer.print p.engines
            writer.print "\n"
        }


    }

    private static int[] xor(List<int[]> input) {
        List<int[]> myList = new ArrayList<int[]>(input);
        int[] result = new int[myList.first().length];
        for (int i = 0; i < result.length; i++) {
            result[i] = 1;
        }
        myList.each { array ->
            for (int i = 0; i < result.length; i++) {
                if (result[i] == 1 && array[i] == 1) {
                    result[i] = 1
                } else {
                    result[i] = 0
                }
            }
        }
        return result
    }

    public static int count(int[] values) {
        int result = 0;
        for (int i = 0; i < values.length; i++) {
            result += values[i]
        }
        return result;
    }

    public static void main(String[] args) {
        new CsvReportToPortabilityData(report: new CsvReportLoader(Paths.get(args[0]), new CsvReport()).load()).toCsvReport(System.out)
    }
}

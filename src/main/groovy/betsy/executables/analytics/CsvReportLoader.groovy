package betsy.executables.analytics

import betsy.executables.analytics.model.*

class CsvReportLoader {

    String csvFile

    CsvReport load() {
        CsvReport report = new CsvReport(file: csvFile)

        new File(csvFile).eachLine { line ->
            String[] fields = line.split(";")
            String testName = fields[0]
            String engineName = fields[1]
            String testGroup = fields[2]
            Integer failedTests = fields[4] as Integer
            Integer totalTests = fields[5] as Integer
            Boolean deployable = fields[6] == "1"

            Group group = report.getGroup(testGroup)
            Engine engine = report.getEngine(engineName)
            Result result = new Result(failed: failedTests, total: totalTests, deployable: deployable)
            Test test = report.getTest(testName)
            test.engineToResult.put(engine, result)
            group.tests.add(test)
        }

        report
    }


}

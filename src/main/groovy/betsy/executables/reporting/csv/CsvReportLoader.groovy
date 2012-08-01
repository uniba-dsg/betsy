package betsy.executables.reporting.csv


class CsvReportLoader {

    CsvReport loadFromCsv(String csvFile) {
        CsvReport report = new CsvReport(file: csvFile)

        new File(csvFile).eachLine { line ->
            String[] fields = line.split(";")
            String testName = fields[0]
            String engineName = fields[1]
            String testGroup = fields[2]
            Integer failedTests = fields[4] as Integer
            Integer totalTests = fields[5] as Integer

            Group group = report.getGroup(testGroup)
            Engine engine = report.getEngine(engineName)
            Result result = new Result(failed: failedTests, total: totalTests)
            Test test = report.getTest(testName)
            test.engineToResult.put(engine, result)
            group.tests.add(test)
        }

        report
    }


}

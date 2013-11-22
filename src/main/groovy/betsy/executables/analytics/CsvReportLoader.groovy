package betsy.executables.analytics

import betsy.executables.analytics.model.*

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class CsvReportLoader {

    Path csvFile

    CsvReport load() {
        CsvReport report = new CsvReport(file: csvFile)

        Files.readAllLines(csvFile, StandardCharsets.UTF_8).each { line ->
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

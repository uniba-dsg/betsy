package betsy.common.model;

import java.nio.file.Path;

public interface TestSuiteFolderStructure {

    default String getCsvFile() {
        return "results.csv";
    }

    default String getCsvDurationFile() {
        return "durations.csv";
    }

    default Path getReportsPath() {
        return getPath().resolve("reports");
    }

    default Path getCsvFilePath() {
        return getReportsPath().resolve(getCsvFile());
    }

    default Path getCsvDurationFilePath() {
        return getPath().resolve(getCsvDurationFile());
    }

    default Path getJUnitXMLFilePath() {
        return getReportsPath().resolve("TESTS-TestSuites.xml");
    }

    Path getPath();

}

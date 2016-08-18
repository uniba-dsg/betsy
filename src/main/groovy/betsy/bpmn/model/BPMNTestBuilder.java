package betsy.bpmn.model;

import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class BPMNTestBuilder {

    public static final String ESCAPED_DOUBLE_QUOTATION_MARK = "\"";

    private final String packageString;

    private final List<Path> logs;

    private final BPMNProcess process;

    public BPMNTestBuilder(String packageString, List<Path> logs, BPMNProcess process) {
        this.packageString = Objects.requireNonNull(packageString);
        this.logs = Objects.requireNonNull(logs);
        this.process = Objects.requireNonNull(process);
    }

    public void buildTests() {
        //Build test for each Test Case
        for (BPMNTestCase testCase : process.getTestCases()) {

            String logFilePath = logs.stream()
                    .filter(path -> Objects.equals(path.getFileName().toString(), "log" + testCase.getNumber() + ".txt"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("log not found"))
                    .toAbsolutePath()
                    .toString()
                    .replaceAll("\\\\","/");

            //assemble array of assertion for unitTestString
            String expectedProcessTrace = getAssertionString(testCase.getAssertions());

            Path testClass = process.getTargetTestSrcPathWithCase(testCase.getNumber()).resolve(packageString.replace(".", "/")).resolve(process.getName() + ".java");

            FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/ProcessTestClass.template"), testClass);

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("PROCESS_NAME", process.getName());
            replacements.put("PACKAGE_STRING", packageString);
            replacements.put("TEST_CASE", testCase.getNormalizedTestCaseName());
            replacements.put("ASSERTION_LIST_STRING", expectedProcessTrace);
            replacements.put("LOGFILE_PATH", logFilePath);

            FileTasks.replaceTokensInFile(testClass, replacements);
        }

    }

    public static String getAssertionString(List<String> assertions) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        for (String string : assertions) {
            joiner.add(ESCAPED_DOUBLE_QUOTATION_MARK + string + ESCAPED_DOUBLE_QUOTATION_MARK);
        }


        return joiner.toString();
    }

}

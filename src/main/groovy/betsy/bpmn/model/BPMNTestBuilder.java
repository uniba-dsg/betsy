package betsy.bpmn.model;

import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class BPMNTestBuilder {
    public void buildTests() {
        //Build test for each Test Case
        for (BPMNTestCase testCase : process.getTestCases()) {

            String logFilePath = logDir.resolve("log" + testCase.getNumber() + ".txt").toUri().toString().substring(8);

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

    public static final String ESCAPED_DOUBLE_QUOTATION_MARK = "\"";

    public String getPackageString() {
        return packageString;
    }

    public void setPackageString(String packageString) {
        this.packageString = packageString;
    }

    public Path getLogDir() {
        return logDir;
    }

    public void setLogDir(Path logDir) {
        this.logDir = logDir;
    }

    public BPMNProcess getProcess() {
        return process;
    }

    public void setProcess(BPMNProcess process) {
        this.process = process;
    }


    private String packageString;
    private Path logDir;
    private BPMNProcess process;
}

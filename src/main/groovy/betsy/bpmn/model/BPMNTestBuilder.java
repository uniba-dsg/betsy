package betsy.bpmn.model;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import betsy.bpmn.engines.TestCaseUtil;
import betsy.common.tasks.FileTasks;
import betsy.common.util.ClasspathHelper;
import pebl.test.TestCase;

public class BPMNTestBuilder {

    public static final String ESCAPED_DOUBLE_QUOTATION_MARK = "\"";

    private String packageString;

    private Path logDir;

    private BPMNProcess process;

    public void buildTests() {
        //Build test for each Test Case
        for (TestCase testCase : process.getTestCases()) {

            String logFilePath = logDir.resolve("log-"+ process.getName()+ "-" + testCase.getNumber() + ".txt").toAbsolutePath().toString().replaceAll("\\\\","/");

            //assemble array of assertion for unitTestString
            String expectedProcessTrace = getAssertionString(TestCaseUtil.getTraceAssertions(testCase));

            Path testClass = process.getTargetTestSrcPathWithCase(testCase.getNumber()).resolve(packageString.replace(".", "/")).resolve(process.getName() + ".java");

            FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/ProcessTestClass.template"), testClass);

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("PROCESS_NAME", process.getName());
            replacements.put("PACKAGE_STRING", packageString);
            replacements.put("TEST_CASE", TestCaseUtil.getNormalizedTestCaseName(testCase));
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

    public void setPackageString(String packageString) {
        this.packageString = packageString;
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

}

package betsy.bpmn.model

import betsy.common.tasks.FileTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path

class BPMNTestBuilder {

    public static final String ESCAPED_DOUBLE_QUOTATION_MARK = "\""

    String packageString
    Path logDir
    BPMNProcess process

    public void buildTests() {
        //Build test for each Test Case
        for (BPMNTestCase testCase : process.testCases) {

            String logFilePath = logDir.resolve("log${testCase.number}.txt").toUri().toString().substring(8)

            //assemble array of assertion for unitTestString
            String assertionListString = getAssertionString(testCase.getAssertions())

            Path testClass = process.getTargetTestSrcPathWithCase(testCase.number).resolve(packageString.replace('.', '/')).resolve(process.name + ".java")

            FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/bpmn/ProcessTestClass.template"), testClass)

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("PROCESS_NAME", process.name)
            replacements.put("PACKAGE_STRING", packageString)
            replacements.put("TEST_CASE", testCase.toString())
            replacements.put("ASSERTION_LIST_STRING", assertionListString)
            replacements.put("LOGFILE_PATH", logFilePath)

            FileTasks.replaceTokensInFile(testClass, replacements)
        }
    }

    public static String getAssertionString(List<String> assertions){
        StringJoiner joiner = new StringJoiner(",", "{", "}")

        for(String string : assertions) {
            joiner.add(ESCAPED_DOUBLE_QUOTATION_MARK + string + ESCAPED_DOUBLE_QUOTATION_MARK);
        }

        return joiner.toString();
    }
}

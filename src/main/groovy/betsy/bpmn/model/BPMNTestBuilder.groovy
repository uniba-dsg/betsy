package betsy.bpmn.model

import betsy.common.tasks.FileTasks
import betsy.common.util.ClasspathHelper

import java.nio.file.Path

class BPMNTestBuilder {

    String packageString
    Path logDir
    BPMNProcess process

    public void buildTests() {
        //Build test for each Test Case
        for (BPMNTestCase testCase : process.testCases) {

            //assemble array of assertion for unitTestString
            String assertionListString = getAssertionString(testCase)

            Path testClass = process.targetTestSrcPath.resolve("case${testCase.number}").resolve(packageString.replace('.', '/')).resolve("${process.name}.java")

            FileTasks.copyFileContentsToNewFile(ClasspathHelper.getFilesystemPathFromClasspathPath("/ProcessTestClass.txt"), testClass)

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("PROCESS_NAME", process.name)
            replacements.put("PACKAGE_STRING", packageString)
            replacements.put("TEST_CASE", testCase.toString())
            replacements.put("ASSERTION_LIST_STRING", assertionListString)

            FileTasks.replaceTokensInFile(testClass, replacements)
        }
    }

    private String getAssertionString(BPMNTestCase testCase){
        List<String> assertionList = testCase.assertions

        String result = "{";

        if (assertionList.size() > 0) {
            for (String assertString : assertionList) {
                result = result + "\"" + assertString + "\","
            }
            result = result.substring(0, (result.length() - 1))
        }

        result = result + "}"
        return result
    }
}

package betsy.bpmn.model

import betsy.common.tasks.FileTasks

import java.nio.file.Path

class BPMNTestBuilder {

    String packageString
    Path logDir
    BPMNProcess process

    public void buildTests() {
        //Build test for each Test Case
        for (BPMNTestCase testCase : process.testCases) {

            Path logFile = logDir.resolve("log${testCase.number}.txt")

            List<String> assertionList = testCase.assertions

            //assemble array of assertion for unitTestString
            String assertionListString = "{";
            if (assertionList.size() > 0) {
                for (String assertString : assertionList) {
                    assertionListString = assertionListString + "\"" + assertString + "\","
                }
                assertionListString = assertionListString.substring(0, (assertionListString.length() - 1))
            }
            assertionListString = assertionListString + "}"

            String unitTestString = """package ${packageString};

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

public class ${process.name} {

    static BufferedReader br;
    String[] assertionList = ${assertionListString};

    @BeforeClass
    public static void setup(){
        try {
            br = new BufferedReader(new FileReader("${logFile.toUri().toString().substring(8)}"));
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
    }

    @Test
    public void ${testCase.toString()}(){
        String line;
        List<String> valueList = new ArrayList<String>();
        Boolean result = false;

        //assemble list of returned values
        try{
             while ((line = br.readLine()) != null){
                 valueList.add(line);
             }
        } catch (IOException e){
            e.printStackTrace();
        }

        //check if the asserted count of values exists
        String message = "[Assertion List: ";
        for(String s: assertionList){
            message += s + " ";
        }
        message += " | Value List: ";
        for(String s: valueList){
            message += s + " ";
        }
        message += "]";
        assertEquals(message, assertionList.length, valueList.size());

        //check if all asserted Elements are also in the returned values
        for(String asrt: assertionList){
            for(String val: valueList){
                if(val.contentEquals(asrt)){
                    result = true;
                    valueList.remove(val);
                    break;
                }else{
                    result = false;
                }
            }
            assertTrue("Could not find '" + asrt + "' in value list. " + message, result);
        }
    }

    @AfterClass
    public static void tearDown(){
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(${process.name}.class);
    }

}
"""
            FileTasks.createFile(process.targetTestSrcPath.resolve("case${testCase.number}").resolve(packageString.replace('.', '/')).resolve("${process.name}.java"),
                    unitTestString);
        }
    }
}

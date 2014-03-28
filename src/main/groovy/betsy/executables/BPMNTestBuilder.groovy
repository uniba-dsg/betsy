package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

import java.nio.file.Path
import java.nio.file.Paths

class BPMNTestBuilder {

    private static final AntBuilder ant = AntUtil.builder()

    String packageString
    Path logDir
    BPMNProcess process

    public void buildTests() {



        //Build test for each Test Case
        for(BPMNTestCase testCase: process.testCases){

            Path logFile = logDir.resolve("log${testCase.number}.txt")

            List<String> assertionList = testCase.assertions

            //assemble array of assertion for unitTestString
            String assertionListString = "{";
            if(assertionList.size() > 0 ){
                for(String assertString: assertionList){
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
            ant.echo(message: unitTestString, file: Paths.get("${process.targetTestSrcPath}/case${testCase.number}/${packageString.replace('.', '/')}/${process.name}.java"))
        }
    }
}

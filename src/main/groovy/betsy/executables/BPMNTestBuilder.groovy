package betsy.executables

import ant.tasks.AntUtil
import betsy.data.BPMNProcess
import betsy.data.BPMNTestCase

import java.nio.file.Path
import java.nio.file.Paths

class BPMNTestBuilder {

    private static final AntBuilder ant = AntUtil.builder()

    String packageString
    String name
    Path logDir
    String unitTestDir
    BPMNProcess process

    public void buildTests() {



        //Build test for each Test Case
        for(BPMNTestCase testCase: process.testCases){

            Path logFile = logDir.resolve("log${testCase.number}.txt")

            List<String> assertionList = testCase.testSteps.get(0).assertions

            //assemble array of assertion for unitTestString
            String assertionListString = "{";
            for(String assertString: assertionList){
                assertionListString = assertionListString + "\"" + assertString + "\","
            }
            assertionListString = assertionListString.substring(0, (assertionListString.length() - 1))
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

public class ${name} {

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
    public void testIfSuccessful(){
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
        assertEquals(valueList.size(), assertionList.length);

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
            assertTrue(result);
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
        return new JUnit4TestAdapter(${name}.class);
    }

}
"""
            ant.echo(message: unitTestString, file: Paths.get("${unitTestDir}/case${testCase.number}/${packageString.replace('.', '/')}/${name}.java"))
        }
    }
}

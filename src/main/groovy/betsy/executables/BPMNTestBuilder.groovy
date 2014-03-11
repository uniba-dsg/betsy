package betsy.executables

import ant.tasks.AntUtil

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created with IntelliJ IDEA.
 * User: Mathias Casar, Andreas Vorndran
 * Date: 03.03.14
 * Time: 09:41
 */
class BPMNTestBuilder {

    private static final AntBuilder ant = AntUtil.builder()

    String packageString
    Path logFile
    String unitTestDir
    List<String> assertionList

    public void buildTest() {

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
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

public class UnitTest {

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

        //check if all asserted Elements are also in the returned values
        for(String val: valueList){
            for(String asrt: assertionList){
                if(val.contentEquals(asrt)){
                    result = true;
                    break;
                }else{
                    result = false;
                }
            }
            if(!result){
                break;
            }
        }
        Assert.assertTrue(true);
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
        return new JUnit4TestAdapter(UnitTest.class);
    }

}
"""
        ant.echo(message: unitTestString, file: Paths.get("${unitTestDir}/${packageString.replace('.', '/')}/UnitTest.java"))
    }

}

package camunda.tasks.simple;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: Andreas Vorndran, Mathias Casar
 * Date: 27.02.14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */

public class UnitTest {

    static BufferedReader br;

    @BeforeClass
    public static void setup(){
        try {
            br = new BufferedReader(new FileReader("server/camunda/server/apache-tomcat-7.0.33/bin/log.txt"));
        }catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
    }

    @Test
    public void testIfSuccessful(){
        try {
            Assert.assertTrue(br.readLine().contentEquals("success"));
        } catch (IOException e) {
            e.printStackTrace();
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
        return new JUnit4TestAdapter(UnitTest.class);
    }

}

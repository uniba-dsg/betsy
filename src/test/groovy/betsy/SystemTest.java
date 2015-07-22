package betsy;

import betsy.Main;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SystemTest {

    @Test
    public void testBpmnActivitiSequenceFlow() throws IOException {
        Main.main("bpmn", "activiti", "SequenceFlow");

        assertEquals("[SequenceFlow;activiti;basics;1;0;1;1]", Files.readAllLines(Paths.get("test/reports/results.csv")).toString());
    }

    /*
    @Test
    public void testBpmnHelp() throws IOException {
        Main.main("bpmn", "--help");

        // no exception is good enough for this
    }
    */

}

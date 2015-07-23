package betsy;

import org.junit.Ignore;
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

    @Test @Ignore(value = "soapUI does not clean up its file resources, causing 'test/' folder to be undeletable on windows")
    public void testBpelOdeSequence() throws IOException, InterruptedException {
        Main.main("bpel", "ode", "sequence");

        assertEquals("[Sequence;ode;structured;1;0;1;1]", Files.readAllLines(Paths.get("test/reports/results.csv")).toString());

        Thread.sleep(15_000); // to release any file locks that may be hold, but this does not help!
    }

}

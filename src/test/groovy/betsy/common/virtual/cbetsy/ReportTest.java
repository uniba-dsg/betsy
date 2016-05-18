package betsy.common.virtual.cbetsy;

import betsy.common.virtual.cbetsy.Reporter;
import betsy.common.virtual.cbetsy.WorkerTemplateGenerator;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class ReportTest {
    @Test
    public void createReport() throws Exception {
        Reporter.createReport(new WorkerTemplateGenerator("bpmn", "all", "ACTIVITIES"), 0, 0, 0 ,0);
        assertTrue("", Paths.get("results").resolve("result.html").toFile().exists());
    }
}
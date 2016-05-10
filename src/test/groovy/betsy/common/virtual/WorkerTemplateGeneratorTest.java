package betsy.common.virtual;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTemplateGeneratorTest {

    @Test
    public void getWorkerTemplates() throws Exception {
        String[] args = {"bpel", "ode", "sequence"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The size should be the same.", 1, templateGenerator.getWorkerTemplates().size());
    }

    @Test
    public void getEngines() throws Exception {
        String[] args = {"bpel", "ode", "sequence"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The hashMap should contain the key.", true, templateGenerator.getEngines().containsKey("ode__1_3_6"));
    }
}
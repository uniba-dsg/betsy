package betsy.common.virtual.cbetsy;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTemplateGeneratorTest {


    @Test
    public void constructorAll() throws Exception {
        String[] args = {"all", "all"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertTrue("The size should be greater 0.", templateGenerator.getBPMNProcesses().size() > 0);
        assertTrue("The size should be greater 0.", templateGenerator.getBPELProcesses().size() > 0);
    }

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
        assertEquals("The values should be equal.", 1, templateGenerator.getEngines().size());
    }

    @Test
    public void getSortedTemplates() throws Exception {
        String[] args = {"bpel", "ode", "sequence"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertTrue("The memory should be greater than 0.", templateGenerator.getSortedTemplates().get(0).getDockerEngine().getMemory() > 0);
    }

    @Test
    public void getEnginesWithValues() throws Exception {
        String[] args = {"bpel", "ode", "sequence"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        ArrayList<DockerEngine> engines = new ArrayList<>(templateGenerator.getEnginesWithValues());
        assertTrue("The memory should be greater than 0.", engines.get(0).getMemory() > 0);
    }

    @Test
    public void getBPELEngines() throws Exception {
        String[] args = {"bpel", "ode", "sequence"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The method should return one engine.",1, templateGenerator.getBPELEngines().size());
    }

    @Test
    public void getBPMNEngines() throws Exception {
        String[] args = {"bpmn", "camunda", "SequenceFlow"};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The method should return one engine.",1, templateGenerator.getBPMNEngines().size());
    }

    @Test
    public void getProcesses() throws Exception {
        String process = "Sequence";
        String[] args = {"bpel", "ode", process};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The processes should be equal.", process, templateGenerator.getProcesses().get(0));
    }

    @Test
    public void getBPELProcesses() throws Exception {
        String process = "Sequence";
        String[] args = {"bpel", "ode", process};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The processes should be equal.", process, templateGenerator.getBPELProcesses().get(0));
    }

    @Test
    public void getBPMNProcesses() throws Exception {
        String process = "SequenceFlow";
        String[] args = {"bpmn", "camunda", process};
        WorkerTemplateGenerator templateGenerator = new WorkerTemplateGenerator(args);
        assertEquals("The processes should be equal.", process, templateGenerator.getBPMNProcesses().get(0));
    }
}
package betsy.common.virtual.cbetsy;

import betsy.common.model.input.EngineIndependentProcess;
import configuration.bpel.BPELProcessRepository;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class WorkerTemplateTest {
    @Test
    public void getDockerEngine() throws Exception {
        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processes = repository.getByName("MINIMAL");
        DockerEngine engine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPEL);
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0).getName(), engine);
        assertEquals("The processes should be equal.", processes.get(0), workerTemplate.getProcess());
        assertEquals("The engines should be equal.", engine, workerTemplate.getDockerEngine());
    }

    @Test
    public void getProcess() throws Exception {
        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processes = repository.getByName("MINIMAL");
        DockerEngine engine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPEL);
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0).getName(), engine);
        assertEquals("The processes should be equal.", processes.get(0), workerTemplate.getProcess());

    }

    @Test
    public void getCmdBPEL() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processes = processRepository.getByName("MINIMAL");
        String name = "test";
        DockerEngine engine = new DockerEngine(name, DockerEngine.TypeOfEngine.BPEL);

        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0).getName(), engine);
        String cmd = "bpel" + engine.getName() + processes.get(0).getName();
        assertEquals("The commands should be the same.", cmd, workerTemplate.getCmd()[0] + workerTemplate.getCmd()[1] + workerTemplate.getCmd()[2]);
    }

    @Test
    public void getCmdBPMN() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processes = processRepository.getByName("MINIMAL");
        DockerEngine engine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPMN);
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0).getName(), engine);
        String cmd = "bpmn" + engine.getName() + processes.get(0).getName();
        assertEquals("The commands should be the same.", cmd, workerTemplate.getCmd()[0] + workerTemplate.getCmd()[1] + workerTemplate.getCmd()[2]);
    }

    @Test
    public void getID() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<EngineIndependentProcess> processes = processRepository.getByName("MINIMAL");
        DockerEngine engine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPMN);
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0).getName(), engine);
        String id = engine.getName().replace("_", "") + processes.get(0).getName().replace("_", "");
        assertEquals("The id should be the same.", id, workerTemplate.getID());
    }
}
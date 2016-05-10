package betsy.common.virtual;

import betsy.bpel.engines.AbstractBPELEngine;
import betsy.bpel.engines.bpelg.BpelgEngine;
import betsy.bpel.model.BPELProcess;
import betsy.bpel.repositories.BPELEngineRepository;
import betsy.bpmn.engines.AbstractBPMNEngine;
import betsy.bpmn.repositories.BPMNEngineRepository;
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
    public void getProcess() throws Exception {
        BPELProcessRepository repository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = repository.getByName("MINIMAL");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), new BpelgEngine());
        assertEquals("The processes should be equal.", processes.get(0) , workerTemplate.getProcess());
    }

    @Test
    public void getBPELEngine() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPELEngineRepository engineRepository = new BPELEngineRepository();
        List<AbstractBPELEngine> engines = engineRepository.getByName("ode");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        assertEquals("The engines should be equal.", engines.get(0) , workerTemplate.getBPELEngine());
    }

    @Test
    public void getBPMNEngine() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        assertEquals("The engines should be equal.", engines.get(0) , workerTemplate.getBPMNEngine());
    }

    @Test
    public void getEngineName() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        assertEquals("The names should be equal.", engines.get(0).getName() , workerTemplate.getBPMNEngine().getName());
    }

    @Test
    public void getTime() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        assertEquals("The time should initial be 0.", 0 , workerTemplate.getTime());
    }

    @Test
    public void setTime() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        long time = 100;
        workerTemplate.setTime(time);
        assertEquals("The times should be the same.", time , workerTemplate.getTime());
    }

    @Test
    public void getCmd() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        String cmd = "bpmn" +engines.get(0).getName() + processes.get(0).getName();
        assertEquals("The commands should be the same.", cmd , workerTemplate.getCmd()[0]+workerTemplate.getCmd()[1]+workerTemplate.getCmd()[2]);
    }

    @Test
    public void getID() throws Exception {
        BPELProcessRepository processRepository = BPELProcessRepository.INSTANCE;
        List<BPELProcess> processes = processRepository.getByName("MINIMAL");
        BPMNEngineRepository engineRepository = new BPMNEngineRepository();
        List<AbstractBPMNEngine> engines = engineRepository.getByName("jbpm");
        WorkerTemplate workerTemplate = new WorkerTemplate(processes.get(0), engines.get(0));
        String id = engines.get(0).getName().replace("_", "") + processes.get(0).getName().replace("_", "");
        assertEquals("The id should be the same.", id , workerTemplate.getID());
    }
}
package betsy.common.virtual.calibration;

import betsy.common.model.input.EngineIndependentProcess;
import betsy.common.virtual.DockerEngine;
import betsy.common.virtual.WorkerTemplate;
import org.junit.Test;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class DockerPropertiesTest {
    @Test
    public void readWorkerTemplates() throws Exception {
        Path path = Paths.get("test.properties");
        HashSet<DockerEngine> engines = new HashSet<>();
        DockerEngine dockerEngine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPEL);
        long time = 100;
        int memory = 400;
        dockerEngine.setTime(time);
        dockerEngine.setMemory(memory);
        engines.add(dockerEngine);
        DockerProperties.writeEngines(path, engines);
        dockerEngine.setTime(200);
        dockerEngine.setMemory(500);

        EngineIndependentProcess process = Mockito.mock(EngineIndependentProcess.class);
        WorkerTemplate workerTemplate = new WorkerTemplate(process, dockerEngine);
        ArrayList<WorkerTemplate> workerTemplates = new ArrayList<>();
        workerTemplates.add(workerTemplate);
        workerTemplates = DockerProperties.readWorkerTemplates(path, workerTemplates);
        assertEquals("The time have to be equal", time, workerTemplates.get(0).getDockerEngine().getTime());
        assertEquals("The memories have to be equal", memory, workerTemplates.get(0).getDockerEngine().getMemory());
    }

    @Test
    public void readEngines() throws Exception {
        Path path = Paths.get("test.properties");
        HashSet<DockerEngine> engines = new HashSet<>();
        DockerEngine dockerEngine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPEL);
        long time = 100;
        int memory = 400;
        dockerEngine.setTime(time);
        dockerEngine.setMemory(memory);
        engines.add(dockerEngine);
        DockerProperties.writeEngines(path, engines);
        dockerEngine.setTime(200);
        dockerEngine.setMemory(300);
        ArrayList<DockerEngine> engineList = new ArrayList<>(DockerProperties.readEngines(path, engines));
        assertEquals("The times have to be equal.", time, engineList.get(0).getTime());
        assertEquals("The memories have to be equal", memory, engineList.get(0).getMemory());
        path.toFile().delete();
    }

    @Test
    public void writeEngines() throws Exception {
        Path path = Paths.get("test.properties");
        HashSet<DockerEngine> engines = new HashSet<>();
        DockerEngine dockerEngine = new DockerEngine("test", DockerEngine.TypeOfEngine.BPEL);
        long time = 100;
        int memory = 400;
        dockerEngine.setTime(time);
        dockerEngine.setMemory(memory);
        engines.add(dockerEngine);
        DockerProperties.writeEngines(path, engines);
        dockerEngine.setTime(200);
        dockerEngine.setMemory(300);
        ArrayList<DockerEngine> engineList = new ArrayList<>(DockerProperties.readEngines(path, engines));
        assertEquals("The times have to be equal.", time, engineList.get(0).getTime());
        assertEquals("The memories have to be equal", memory, engineList.get(0).getMemory());
        path.toFile().delete();
    }
}
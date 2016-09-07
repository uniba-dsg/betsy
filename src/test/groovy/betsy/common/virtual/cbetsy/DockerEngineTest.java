package betsy.common.virtual.cbetsy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class DockerEngineTest {
    @Test
    public void getName() throws Exception {
        String name = "engine";
        DockerEngine dockerEngine = new DockerEngine(name, DockerEngine.TypeOfEngine.BPEL);
        assertEquals("The names have to be equal.", name, dockerEngine.getName());

    }

    @Test
    public void getTypeOfEngine() throws Exception {
        DockerEngine.TypeOfEngine type = DockerEngine.TypeOfEngine.BPEL;
        DockerEngine dockerEngine = new DockerEngine("engine", type);
        assertEquals("The types have to be equal", type, dockerEngine.getTypeOfEngine());
    }

    @Test
    public void getMemory() throws Exception {
        DockerEngine dockerEngine = new DockerEngine("engine", DockerEngine.TypeOfEngine.BPEL);
        int memory = 2000;
        dockerEngine.setMemory(memory);
        assertEquals("The values have to be equal.", memory, dockerEngine.getMemory());
    }

    @Test
    public void setMemory() throws Exception {
        DockerEngine dockerEngine = new DockerEngine("engine", DockerEngine.TypeOfEngine.BPEL);
        int memory = 2000;
        dockerEngine.setMemory(memory);
        assertEquals("The values have to be equal.", memory, dockerEngine.getMemory());
    }

    @Test
    public void getTime() throws Exception {
        DockerEngine dockerEngine = new DockerEngine("engine", DockerEngine.TypeOfEngine.BPEL);
        long time = 2000;
        dockerEngine.setTime(time);
        assertEquals("The values have to be equal.", time, dockerEngine.getTime());
    }

    @Test
    public void setTime() throws Exception {
        DockerEngine dockerEngine = new DockerEngine("engine", DockerEngine.TypeOfEngine.BPEL);
        long time = 2000;
        dockerEngine.setTime(time);
        assertEquals("The values have to be equal.", time, dockerEngine.getTime());
    }

}
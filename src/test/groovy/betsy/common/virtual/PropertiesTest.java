package betsy.common.virtual;

import betsy.bpel.engines.ode.Ode136Engine;
import betsy.bpel.model.BPELProcess;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class PropertiesTest {

    @Test
    public void read() throws Exception {
        Path path = Paths.get("test.properties");
        ArrayList<String[]> values = new ArrayList<>();
        Long time = 1000L;
        Long memory = 1000L;
        values.add(new String[] {"ode__1_3_6", time.toString(), memory.toString()});
        Properties.write(path, values);

        WorkerTemplate workerTemplate = new WorkerTemplate(new BPELProcess(), new Ode136Engine());
        ArrayList<WorkerTemplate> workerTemplates = new ArrayList<>();
        workerTemplates.add(workerTemplate);
        workerTemplates = Properties.read(path, workerTemplates);
        assertEquals("The values should be the same.", time.longValue(), workerTemplates.get(0).getTime());
        assertEquals("The values should be the same.", 0, memory.doubleValue(), workerTemplates.get(0).getMemory());
        path.toFile().delete();
    }

    @Test
    public void write() throws Exception {
        Path path = Paths.get("test.properties");
        ArrayList<String[]> values = new ArrayList<>();
        values.add(new String[] {"ode136", "1000", "1000"});
        Properties.write(path, values);
        assertTrue("The file have to exist.", path.toFile().exists());
        path.toFile().delete();
    }
}
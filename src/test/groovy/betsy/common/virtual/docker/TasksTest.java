package betsy.common.virtual.docker;

import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TasksTest {

    private DockerMachine dockerMachine;

    @Before
    public void setUp() throws Exception {
        dockerMachine = DockerMachines.create(get("dockermachine.test.name"), get("dockermachine.test.ram"), get("dockermachine.test.cpu"));
        dockerMachine.start();
    }

    @After
    public void tearDown() throws Exception {
        DockerMachines.remove(dockerMachine);
    }

    @Test
    public void isDockerInstalled() throws Exception {
        assertTrue("If docker is installed, the method should return true.", Tasks.isDockerInstalled());
    }

    @Test
    public void doDockerMachineTaskWithOutput() throws Exception {
        Scanner scanner = Tasks.doDockerMachineTaskWithOutput("all");
        assertTrue("The scanner should have a next line.", scanner.hasNextLine());

    }

    @Test
    public void doDockerMachineTask() throws Exception {
        String nameOfTheMachine = "testMachine";
        Tasks.doDockerMachineTask("create", "--driver", "virtualbox", nameOfTheMachine);
        HashMap<String, DockerMachine> dockerMachines = DockerMachines.getAll();
        assertEquals("", nameOfTheMachine, dockerMachines.get(nameOfTheMachine).getName());
        DockerMachines.remove(DockerMachines.getAll().get(nameOfTheMachine));
    }

    @Test
    public void doDockerTaskWithOutput() throws Exception {
        String[] cmds = {"ps", "--all"};
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
        while (scanner.get().hasNextLine()) {
            assertTrue("", scanner.get().nextLine().contains("CONTAINER ID"));
        }
    }

    @Test
    public void doDockerTask() throws Exception {
        String[] cmds = {"run", "hello-world"};
        Tasks.doDockerTask(dockerMachine, cmds);
        assertEquals("", 1, Containers.getAll(dockerMachine).size());
    }

    @Test
    public void doImageTask() throws Exception {
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        assertEquals("There should be two engines installed.", 2, Images.getAll(dockerMachine).size());
    }

    @Test
    public void doEngineImageTask() throws Exception {
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        Images.buildEngine(dockerMachine, Paths.get("docker/image/engine"), "ode__1_3_5");
        assertEquals("There should be three engines installed.", 3, Images.getAll(dockerMachine).size());
    }

    @Test
    public void doDockerCreateRunTask() throws Exception {
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        String[] args = {"bpel", "ode__1_3_5", "sequence"};
        String[] commands = {"create", "betsy", "betsy"};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerCreateRunTask(dockerMachine, cmds);
        assertEquals("", 1, Containers.getAll(dockerMachine).size());
    }

    @Test
    public void doDockerCreateRunTaskWithConstraints() throws Exception {
        Images.build(dockerMachine, Paths.get("docker/image/betsy").toAbsolutePath(), "betsy");
        String[] args = {"bpel", "ode__1_3_5", "sequence"};
        String[] commands = {"create", "betsy", String.valueOf(1260), String.valueOf(100), String.valueOf(2000), "betsy"};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerCreateRunTaskWithConstraints(dockerMachine, cmds);
        assertEquals("", 1, Containers.getAll(dockerMachine).size());
    }

}
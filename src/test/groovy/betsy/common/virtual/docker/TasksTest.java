package betsy.common.virtual.docker;

import betsy.common.tasks.WaitTasks;
import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
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
    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");


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
        if (System.getProperty("os.name").contains("Windows")) {
            Scanner scanner = Tasks.doDockerMachineTaskWithOutput("all");
            assertTrue("The scanner should have a next line.", scanner.hasNextLine());
        }

    }

    @Test
    public void doDockerMachineTask() throws Exception {
        if (System.getProperty("os.name").contains("Windows")) {
            String nameOfTheMachine = "testMachine";
            Tasks.doDockerMachineTask("create", "--driver", "virtualbox", nameOfTheMachine);
            HashMap<String, DockerMachine> dockerMachines = DockerMachines.getAll();
            assertEquals("The names should be equal.", nameOfTheMachine, dockerMachines.get(nameOfTheMachine).getName());

            DockerMachines.remove(DockerMachines.getAll().get(nameOfTheMachine));
        }
    }

    @Test
    public void doDockerTaskWithOutput() throws Exception {
        String[] cmds = {"ps", "--all"};
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
        boolean taskWasExecuted = false;
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                if (scanner.get().nextLine().contains("CONTAINER ID")) {
                    taskWasExecuted = true;
                }
            }
        }
        assertTrue("If the task was executed, the value has to be true.", taskWasExecuted);
    }

    @Test
    public void doDockerTask() throws Exception {
        int size = Containers.getAll(dockerMachine).size();
        String name = "test";
        java.util.Optional<Container> container = java.util.Optional.ofNullable(Containers.getAll(dockerMachine).get(name));
        if (!container.isPresent()) {
            String[] cmds = {"run", "--name", name, "hello-world"};
            Tasks.doDockerTask(dockerMachine, cmds);
            assertEquals("The values have to be equal.", ++size, Containers.getAll(dockerMachine).size());
            Containers.remove(dockerMachine, Containers.getAll(dockerMachine).get(name));
        }
    }

    @Test
    public void doImageTask() throws Exception {
        String name = "test";
        java.util.Optional<Image> image = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("test"));
        if (!image.isPresent()) {
            int size = Images.getAll(dockerMachine).size();
            Image betsy = Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), name);
            assertEquals("The values have to be equal.", ++size, Images.getAll(dockerMachine).size());
            Images.remove(dockerMachine, betsy);
        }
    }

    @Test
    public void doEngineImageTask() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasBuild = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasBuild = false;
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        boolean engineImageWasCreated = false;
        if (engineImage.isPresent()) {
            engineImageWasCreated = true;
            Images.remove(dockerMachine, engineImage.get());
        }

        int size = Images.getAll(dockerMachine).size();
        Images.buildEngine(dockerMachine, images.resolve("engine"), "ode__1_3_5");
        assertEquals("The values should be equal.", ++size, Images.getAll(dockerMachine).size());
        engineImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ode135"));
        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(dockerMachine, engineImage.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasBuild) {
            Images.remove(dockerMachine, betsyImage.get());
            java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                Images.remove(dockerMachine, ubuntuImage.get());
            }
        }
    }

    @Test
    public void doDockerCreateRunTask() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasBuild = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasBuild = false;
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }

        java.util.Optional<Container> betsyContainer = java.util.Optional.ofNullable(Containers.getAll(dockerMachine).get("betsy"));
        if (betsyContainer.isPresent()) {
            Containers.remove(dockerMachine, betsyContainer.get());
        }
        int size = Containers.getAll(dockerMachine).size();
        String[] args = {"sh", "betsy", "bpel", "ode__1_3_5", "sequence"};
        String[] commands = {"create", "betsy", "betsy"};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerCreateRunTask(dockerMachine, cmds);
        WaitTasks.sleep(500);
        assertEquals("The values have to be equal.", ++size, Containers.getAll(dockerMachine).size());

        if (betsyContainer.isPresent()) {
            Containers.remove(dockerMachine, betsyContainer.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasBuild) {
            Images.remove(dockerMachine, betsyImage.get());
            java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                Images.remove(dockerMachine, ubuntuImage.get());
            }
        }
    }

    @Test
    public void doDockerCreateRunTaskWithConstraints() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("betsy"));
        boolean betsyImageWasBuild = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasBuild = false;
            Images.build(dockerMachine, images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Container> betsyContainer = java.util.Optional.ofNullable(Containers.getAll(dockerMachine).get("betsy"));
        if (betsyContainer.isPresent()) {
            Containers.remove(dockerMachine, betsyContainer.get());
        }

        int size = Containers.getAll(dockerMachine).size();
        String[] args = {"bpel", "ode__1_3_5", "sequence"};
        String[] commands = {"create", "betsy", String.valueOf(100), String.valueOf(2000), "betsy"};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerCreateRunTaskWithConstraints(dockerMachine, cmds);
        WaitTasks.sleep(500);
        assertEquals("The values have to be equal.", ++size, Containers.getAll(dockerMachine).size());

        betsyContainer = java.util.Optional.ofNullable(Containers.getAll(dockerMachine).get("betsy"));
        if (betsyContainer.isPresent()) {
            Containers.remove(dockerMachine, betsyContainer.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasBuild) {
            Images.remove(dockerMachine, betsyImage.get());
            java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll(dockerMachine).get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                Images.remove(dockerMachine, ubuntuImage.get());
            }
        }
    }

}
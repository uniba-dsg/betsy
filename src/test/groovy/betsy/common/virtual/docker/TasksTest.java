package betsy.common.virtual.docker;

import betsy.common.tasks.WaitTasks;
import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static betsy.common.config.Configuration.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class TasksTest {

    private Path docker = Paths.get(get("docker.dir"));
    private Path images = docker.resolve("image");


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isDockerInstalled() throws Exception {
        assertTrue("If docker is installed, the method should return true.", Tasks.isDockerInstalled());
    }


    @Test
    public void doDockerTaskWithOutput() throws Exception {
        String[] cmds = {"ps", "--all"};
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerTaskWithOutput(cmds));
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
        int size = Containers.getAll().size();
        String name = "test";
        java.util.Optional<Container> container = java.util.Optional.ofNullable(Containers.getAll().get(name));
        if (!container.isPresent()) {
            String[] cmds = {"run", "--name", name, "hello-world"};
            Tasks.doDockerTask(cmds);
            assertEquals("The values have to be equal.", ++size, Containers.getAll().size());
            Containers.remove(Containers.getAll().get(name));
        }
    }

    @Test
    public void doEngineImageTask() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasBuild = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasBuild = false;
            Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Image> engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode135"));
        boolean engineImageWasCreated = false;
        if (engineImage.isPresent()) {
            engineImageWasCreated = true;
            Images.remove(engineImage.get());
        }

        int size = Images.getAll().size();
        Images.buildEngine(images.resolve("engine").toAbsolutePath(), "ode__1_3_5");
        assertEquals("The values should be equal.", ++size, Images.getAll().size());
        engineImage = java.util.Optional.ofNullable(Images.getAll().get("ode135"));
        if (engineImage.isPresent() && !engineImageWasCreated) {
            Images.remove(engineImage.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasBuild) {
            Images.remove(betsyImage.get());
            java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                Images.remove(ubuntuImage.get());
            }
        }
    }

    @Test
    public void doDockerCreateRunTaskWithConstraints() throws Exception {
        java.util.Optional<Image> betsyImage = java.util.Optional.ofNullable(Images.getAll().get("betsy"));
        boolean betsyImageWasBuild = true;
        if (!betsyImage.isPresent()) {
            betsyImageWasBuild = false;
            Images.build(images.resolve("betsy").toAbsolutePath(), "betsy");
        }
        java.util.Optional<Container> betsyContainer = java.util.Optional.ofNullable(Containers.getAll().get("betsy"));
        if (betsyContainer.isPresent()) {
            Containers.remove(betsyContainer.get());
        }

        int size = Containers.getAll().size();
        String[] args = {"bpel", "ode__1_3_5", "sequence"};
        String[] commands = {"create", "betsy", String.valueOf(100), String.valueOf(2000), "betsy"};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        Tasks.doDockerCreateRunTaskWithConstraints(cmds);
        WaitTasks.sleep(500);
        assertEquals("The values have to be equal.", ++size, Containers.getAll().size());

        betsyContainer = java.util.Optional.ofNullable(Containers.getAll().get("betsy"));
        if (betsyContainer.isPresent()) {
            Containers.remove(betsyContainer.get());
        }
        if (betsyImage.isPresent() && !betsyImageWasBuild) {
            Images.remove(betsyImage.get());
            java.util.Optional<Image> ubuntuImage = java.util.Optional.ofNullable(Images.getAll().get("ubuntu"));
            if (ubuntuImage.isPresent()) {
                Images.remove(ubuntuImage.get());
            }
        }
    }
}
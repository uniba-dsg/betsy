package betsy.common.virtual.docker;

import betsy.common.virtual.exceptions.DockerException;
import com.google.common.base.Optional;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 *
 * This class has methods to interact with a {@link Container} like create, copy and so on.
 *
 */
public class Containers {

    private static final Logger LOGGER = Logger.getLogger(Containers.class);

    /**
     * This method creates a {@link Container}.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container create(DockerMachine dockerMachine, String containerName, String imageName, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"create", containerName, imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String id = null;
        Container container;
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerCreateRunTask(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                id = scanner.get().nextLine();
            }
            container = new Container(id, containerName, dockerMachine);
            dockerMachine.addContainer(container);
        } else {
            throw new DockerException("Executing the task 'create' failed.");
        }
        return container;
    }

    /**
     * This method creates a {@link Container} with constraints.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param memory        The value of the memory.
     * @param hddSpeed      The speed of the hdd.
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container create(DockerMachine dockerMachine, String containerName, String imageName, int memory, int hddSpeed, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"create", containerName, String.valueOf(hddSpeed), String.valueOf(memory), imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String id = null;
        Container container;
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerCreateRunTaskWithConstraints(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                id = scanner.get().nextLine();
            }
            container = new Container(id, containerName, dockerMachine);
            dockerMachine.addContainer(container);
        } else {
            throw new DockerException("Executing the task 'create' failed.");
        }
        return container;
    }

    /**
     * This method removes a {@link Container}.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param container     The {@link Container}, which sould be removed.
     */
    public static void remove(DockerMachine dockerMachine, Container container) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(container, "The container can't be null.");
        String[] cmds = {"rm", container.getName()};
        dockerMachine.removeContainer(container);
        Tasks.doDockerTask(dockerMachine, cmds);
    }

    /**
     * This method removes a {@link Container}.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param containers    The containers to remove.
     */
    public static void removeAll(DockerMachine dockerMachine, List<Container> containers) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(containers, "The container can't be null.");
        containers.forEach(e -> {
            String[] cmds = {"rm", e.getName()};
            dockerMachine.removeContainer(e);
            Tasks.doDockerTask(dockerMachine, cmds);
        });
    }

    /**
     * This method reutrns all existing {@link Container}.
     *
     * @return A {@link HashMap} with all {@link Container}.
     */
    public static HashMap<String, Container> getAll(DockerMachine dockerMachine) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        HashMap<String, Container> containers = dockerMachine.getContainers();
        String[] cmds = {"ps", "--all"};
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerTaskWithOutput(dockerMachine, cmds));
        boolean containsContainer = false;
        int beginName = 0;
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                if (nextLine.contains("CONTAINER ID")) {
                    beginName = nextLine.indexOf("NAMES");
                    containsContainer = true;
                }else if (containsContainer) {
                    String id = nextLine.substring(0, nextLine.indexOf(" "));
                    String name = nextLine.substring(beginName);
                    containers.putIfAbsent(name, new Container(id, name, dockerMachine));
                }
            }
        } else {
            throw new DockerException("Executing the task 'ps' failed.");
        }
        return containers;
    }

    /**
     * This method creates and runs a {@link Container}.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param containerName The name of the {@link Container}.
     * @param imageName     The name of the {@link Image}.
     * @param args          Optional arguments for the creation of the container.
     * @return Returns the created {@link Container}.
     */
    public static Container run(DockerMachine dockerMachine, String containerName, String imageName, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"run", containerName, imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String nextLine = null;
        Container container;
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerCreateRunTask(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
            }
            container = new Container(nextLine, containerName, dockerMachine);
            dockerMachine.addContainer(container);
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }
        return container;
    }

    /**
     * This method runs a {@link Container} with constraints.
     *
     * @param dockerMachine The active {@link DockerMachine}.
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param memory        The value of the memory.
     * @param hddSpeed      The speed of the hdd.
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container run(DockerMachine dockerMachine, String containerName, String imageName, int memory, int hddSpeed, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"run", containerName, String.valueOf(hddSpeed), String.valueOf(memory), imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String nextLine = null;
        Container container;
        Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerCreateRunTaskWithConstraints(dockerMachine, cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
            }
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }

        String[] idCommands= {"ps", "-aqf", "name="+containerName};
        Optional<Scanner> scannerId = Optional.fromNullable(Tasks.doDockerTaskWithOutput(dockerMachine, idCommands));
        String id = "";
        if (scannerId.isPresent()) {
            while (scannerId.get().hasNextLine()) {
                id = scannerId.get().nextLine();
            }
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }
        container = new Container(id, containerName, dockerMachine);
        dockerMachine.addContainer(container);

        return container;
    }
}

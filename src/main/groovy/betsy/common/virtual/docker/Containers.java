package betsy.common.virtual.docker;

import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.util.*;

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
    private static HashMap<String, Container> containers = new HashMap<>();

    /**
     * This method creates a {@link Container}.
     *
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container create(String containerName, String imageName, String... args) {
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"create", "--name", containerName, imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String id = null;
        Container container;
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                id = scanner.get().nextLine();
            }
            container = new Container(id, containerName);
            containers.put(containerName, container);
        } else {
            throw new DockerException("Executing the task 'create' failed.");
        }
        return container;
    }

    /**
     * This method creates a {@link Container} with constraints.
     *
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param memory        The value of the memory.
     * @param hddSpeed      The speed of the hdd.
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container create(String containerName, String imageName, int memory, int hddSpeed, String... args) {
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"create", containerName, String.valueOf(hddSpeed), String.valueOf(memory), imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String id = null;
        Container container;
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerCreateRunTaskWithConstraints(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                id = scanner.get().nextLine();
            }
            container = new Container(id, containerName);
            containers.put(containerName, container);
        } else {
            throw new DockerException("Executing the task 'create' failed.");
        }
        return container;
    }

    /**
     * This method removes a {@link Container}.
     *
     * @param container     The {@link Container}, which sould be removed.
     */
    public static void remove(Container container) {
        Objects.requireNonNull(container, "The container can't be null.");
        String[] cmds = {"rm", container.getName()};
        Tasks.doDockerTask(cmds);
        containers.remove(container.getName());

    }

    /**
     * This method removes a {@link Container}.
     *
     * @param containerList    The containers to remove.
     */
    public static void removeAll(List<Container> containerList) {
        Objects.requireNonNull(containerList, "The container can't be null.");
        containerList.forEach(container -> {
            String[] cmds = {"rm", container.getName()};
            Tasks.doDockerTask(cmds);
            Containers.remove(container);
        });
    }

    /**
     * This method reutrns all existing {@link Container}.
     *
     * @return A {@link HashMap} with all {@link Container}.
     */
    public static HashMap<String, Container> getAll() {
        String[] cmds = {"ps", "--all"};
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(cmds));
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
                    Optional<Container> container = Optional.ofNullable(containers.get(name));
                    if(!container.isPresent()){
                        containers.put(name, new Container(id, name));
                    }
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
     * @param containerName The name of the {@link Container}.
     * @param imageName     The name of the {@link Image}.
     * @param args          Optional arguments for the creation of the container.
     * @return Returns the created {@link Container}.
     */
    public static Container run(String containerName, String imageName, String... args) {
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"run", "--name", containerName, imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String nextLine = null;
        Container container;
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerTaskWithOutput(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                nextLine = scanner.get().nextLine();
                LOGGER.info("["+ containerName +"]" + nextLine);
            }
            container = new Container(nextLine, containerName);
            containers.put(container.getName(), container);
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }
        return container;
    }

    /**
     * This method runs a {@link Container} with constraints.
     *
     * @param containerName The name of the {@link Container}
     * @param imageName     The name of the image
     * @param memory        The value of the memory.
     * @param hddSpeed      The speed of the hdd.
     * @param args          Optional arguments for the creation.
     * @return Returns the created {@link Container}.
     */
    public static Container run(String containerName, String imageName, int memory, int hddSpeed, String... args) {
        Objects.requireNonNull(containerName, "The containerName can't be null.");
        Objects.requireNonNull(imageName, "The imageName can't be null.");
        String[] commands = {"run", containerName, String.valueOf(hddSpeed), String.valueOf(memory), imageName};
        String[] cmds = new String[args.length + commands.length];
        System.arraycopy(commands, 0, cmds, 0, commands.length);
        System.arraycopy(args, 0, cmds, commands.length, args.length);
        String nextLine;
        Container container = null;
        Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerCreateRunTaskWithConstraints(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                nextLine = scanner.get().nextLine();
                LOGGER.info("["+ containerName +"]" + nextLine);
            }
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }

        String[] idCommands= {"ps", "-aqf", "name="+containerName};
        Optional<Scanner> scannerId = Optional.ofNullable(Tasks.doDockerTaskWithOutput(idCommands));
        String id;
        if (scannerId.isPresent()) {
            while (scannerId.get().hasNextLine()) {
                id = scannerId.get().nextLine();
                container = new Container(id, containerName);
                containers.put(container.getName(), container);
            }
        } else {
            throw new DockerException("Executing the task 'run' failed.");
        }
        return container;
    }
}

package betsy.common.virtual.docker;

import betsy.common.virtual.exceptions.DockerException;
import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class represents a docker machine.
 */
public class DockerMachine {

    private String nameOfTheMachine;
    private static HashMap<String, Container> containers = new HashMap<>();
    private static HashMap<String, Image> images = new HashMap<>();

    /**
     * @param name The name of the docker machine.
     */
    public DockerMachine(String name) {
        Objects.requireNonNull(name, "The name of the machine can't be null.");
        this.nameOfTheMachine = name;
    }

    /**
     * This method starts the docker machine.
     */
    public void start() {
        String[] cmds = {"start", nameOfTheMachine};
        Tasks.doDockerMachineTask(cmds);
    }

    /**
     * This method stops the docker machine.
     */
    public void stop() {
        String[] cmds = {"stop", nameOfTheMachine};
        Tasks.doDockerMachineTask(cmds);
    }

    /**
     * This method returns the name of the docker machine.
     *
     * @return The name of the docker machine as {@link String}.
     */
    public String getName() {
        return nameOfTheMachine;
    }

    /**
     * This method returns the actual {@link Status} of machine.
     *
     * @return The actual {@link Status} of the {@link DockerMachine}.
     */
    public Status getStatus() {
        if (System.getProperty("os.name").contains("Windows")) {
            String[] cmds = {"status", nameOfTheMachine};
            Optional<Scanner> scanner = Optional.fromNullable(Tasks.doDockerMachineTaskWithOutput(cmds));
            if(scanner.isPresent()) {
                while (scanner.get().hasNextLine()) {
                    String nextLine = scanner.get().nextLine();
                    if (nextLine.contains("Running")) {
                        return Status.RUNNING;
                    }
                }
            }else{
                throw new DockerException("Executing the task 'status' failed.");
            }
            return Status.STOPPED;
        } else {
            return Status.RUNNING;
        }
    }

    /**
     *
     * This method returns the images installed on this machine.
     *
     * @return A {@link HashMap} with images of the {@link DockerMachine}.
     */
    public HashMap<String,Image> getImages() {
        return images;
    }

    /**
     *
     * With this method you can add some image to the dockermachine.
     *
     * @param image image The {@link Image} to add.
     */
    public void addImage(Image image) {
        images.put(image.getName(), image);
    }

    /**
     *
     * With this method you can remove some image from the dockermachine.
     *
     * @param image The {@link Image} to remove.
     */
    public void removeImage(Image image) {
        images.remove(image.getName());
    }

    /**
     *
     * This method returns the containers created on this machine.
     *
     * @return A {@link HashMap} with containers of the {@link DockerMachine}.
     */
    public HashMap<String,Container> getContainers() {
        return containers;
    }

    /**
     *
     * With this method you can add some container to the dockermachine.
     *
     * @param container The {@link Container} to add.
     */
    public void addContainer(Container container) {
        containers.put(container.getName(), container);
    }

    /**
     *
     * With this method you can remove some container from the dockermachine.
     *
     * @param container The {@link Container} to remove.
     */
    public void removeContainer(Container container) {
        containers.remove(container.getName());
    }

    /**
     * The status of a {@link DockerMachine}.
     */
    public enum Status {
        STOPPED, RUNNING
    }
}

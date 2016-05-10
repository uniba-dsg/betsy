package betsy.common.virtual.docker;

import betsy.common.virtual.exceptions.DockerException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 * This class has methods to interact with a {@link DockerMachine} like create, remove and so on.
 *
 */
public class DockerMachines {

    private static HashMap<String, DockerMachine> machines = new HashMap<>();

    /**
     * This method creates {@link DockerMachine} with the given name.
     *
     * @param nameOfTheMachine The name for the {@link DockerMachine}.
     * @return The created {@link DockerMachine}.
     */
    public static DockerMachine create(String nameOfTheMachine, String memory, String cpuCount) {
        Objects.requireNonNull(nameOfTheMachine, "The nameOfTheMachine can't be null.");
        Objects.requireNonNull(nameOfTheMachine, "The nameOfTheMachine can't be null.");
        Objects.requireNonNull(nameOfTheMachine, "The nameOfTheMachine can't be null.");
        String[] cmds = {"create", "--virtualbox-memory", memory,"--virtualbox-cpu-count", cpuCount, "--driver", "virtualbox", nameOfTheMachine};
        Tasks.doDockerMachineTask(cmds);
        DockerMachine dockerMachine = new DockerMachine(nameOfTheMachine);
        machines.put(nameOfTheMachine, dockerMachine);
        return dockerMachine;
    }

    /**
     * This method removes the given {@link DockerMachine}.
     *
     * @param dockerMachine The {@link DockerMachine} to remove.
     */
    public static void remove(DockerMachine dockerMachine) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        String[] cmds = {"rm", "-f", dockerMachine.getName()};
        Tasks.doDockerMachineTask(cmds);
        machines.remove(dockerMachine.getName());
    }

    /**
     * This method returns the {@link DockerMachine} for the given name.
     *
     * @param nameOfTheMachine The name of the {@link DockerMachine}.
     * @return The {@link DockerMachine} for the given name.
     */
    public static DockerMachine get(String nameOfTheMachine) {
        Objects.requireNonNull(nameOfTheMachine, "The nameOfTheMachine can't be null.");
        return machines.get(nameOfTheMachine);
    }

    /**
     * This method returns all existing {@link DockerMachine}.
     *
     * @return A {@link HashMap} with the {@link DockerMachine}.
     */
    public static HashMap<String, DockerMachine> getAll() {
        if (machines == null) {
            machines = new HashMap<>();
        }
        if (System.getProperty("os.name").contains("Windows")) {
            String[] cmds = {"ls"};
            Optional<Scanner> scanner = Optional.ofNullable(Tasks.doDockerMachineTaskWithOutput(cmds));
            if (scanner.isPresent()) {
                int counter = 0;
                while (scanner.get().hasNextLine()) {
                    String nextLine = scanner.get().nextLine();
                    if (counter > 2) {
                        String name = nextLine.substring(0, nextLine.indexOf(" "));
                        machines.putIfAbsent(name, new DockerMachine(name));
                    }
                    counter++;
                }
            }else{
                throw new DockerException("Executing the task 'ps' failed.");
            }
        }else{
            machines.put("betsy", new DockerMachine("betsy"));
        }
        return machines;
    }
}

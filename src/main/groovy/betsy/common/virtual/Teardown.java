package betsy.common.virtual;

import betsy.common.virtual.docker.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class Teardown {

    /**
     *
     * @param args The arguments for execution.
     */
    public static void main(String[] args) {
        if ("stop".equalsIgnoreCase(args[0])) {
            stop(createArgsWithoutFirstValue(args));
        } else if ("remove".equalsIgnoreCase(args[0])) {
            remove(createArgsWithoutFirstValue(args));
        } else {
            printUsage("The third argument must be stop or remove.");
        }
    }

    /**
     *
     * With this method it is possible to stop all dockerMachines or the containers.
     *
     * @param args The arguments for execution.
     */
    private static void stop(String... args) {
        if ("machines".equalsIgnoreCase(args[0])) {
            HashMap<String, DockerMachine> dockerMachines = DockerMachines.getAll();
            dockerMachines.forEach((e, k) -> k.stop());
        } else if ("containers".equalsIgnoreCase(args[0])) {
            List<DockerMachine> dockerMachines = new ArrayList<>();
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.run.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.test.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.calibrate.name")));
            for (DockerMachine dockerMachine : dockerMachines) {
                HashMap<String, Container> containers = Containers.getAll(dockerMachine);
                containers.forEach((e, k) -> k.stop());
            }
        } else {
            printUsage("The fourth argument must be containers or machines.");
        }
    }

    /**
     *
     * With this method it is possible to remove all dockerMachines, images or the containers.
     *
     * @param args The arguments for execution.
     */
    private static void remove(String... args) {
        if ("containers".equalsIgnoreCase(args[0])) {
            List<DockerMachine> dockerMachines = new ArrayList<>();
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.run.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.test.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.calibrate.name")));
            dockerMachines.removeAll(Collections.singleton(null));
            for (DockerMachine dockerMachine : dockerMachines) {
                Containers.removeAll(dockerMachine, new ArrayList<>(Containers.getAll(dockerMachine).values()));
            }
        } else if ("images".equalsIgnoreCase(args[0])) {
            remove("containers");
            List<DockerMachine> dockerMachines = new ArrayList<>();
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.run.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.test.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.calibrate.name")));
            dockerMachines.removeAll(Collections.singleton(null));
            for (DockerMachine dockerMachine : dockerMachines) {
                Images.removeAll(dockerMachine, new ArrayList<>(Images.getAll(dockerMachine).values()));

            }
        } else if ("machines".equalsIgnoreCase(args[0])) {
            List<DockerMachine> dockerMachines = new ArrayList<>();
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.run.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.test.name")));
            dockerMachines.add(DockerMachines.getAll().get(get("dockermachine.calibrate.name")));
            dockerMachines.removeAll(Collections.singleton(null));
            dockerMachines.forEach(DockerMachine::stop);
            dockerMachines.forEach(DockerMachines::remove);
        } else {
            printUsage("The fourth argument must be containers, machines or images.");
        }
    }

    /**
     *
     * This method removes the first of the arguments.
     *
     * @param args The arguments to change.
     * @return The changed arguments.
     */
    private static String[] createArgsWithoutFirstValue(String... args) {
        String[] cmds = new String[args.length - 1];
        System.arraycopy(args, 1, cmds, 0, cmds.length);
        return cmds;
    }

    /**
     *  This method prints the right usage of the arguments.
     */
    private static void printUsage(String message) {
        System.out.println(message);
    }
}

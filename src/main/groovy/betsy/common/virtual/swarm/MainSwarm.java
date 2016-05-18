package betsy.common.virtual.swarm;

import betsy.common.virtual.Teardown;
import betsy.common.virtual.calibration.Calibrator;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          This class starts the client or the host.
 */
public class MainSwarm {

    /**
     * The main method of swarm.
     *
     * @param args The arguments for the execution.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            if ("host".equalsIgnoreCase(args[0])) {
                new Host().start(createArgsWithoutFirstValue(args));
            } else if ("client".equalsIgnoreCase(args[0])) {
                Client.main(createArgsWithoutFirstValue(args));
             Teardown.main(createArgsWithoutFirstValue(args));
            } else {
                printUsage();
            }
        } else {
            printUsage();
        }

    }

    /**
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
     * This method prints the right usage of the arguments.
     */
    private static void printUsage() {
        System.out.println("The second argument must be host or client.");
    }

}

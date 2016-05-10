package betsy.common.virtual;

/**
 * @author Christoph Broeker
 * @version 1.0
 */
public class DockerMain {

    /**
     *
     * The main method of docker.
     *
     * @param args The arguments for the execution.
     */
    public static void main(String[] args) {
        if(args.length > 0) {
            if ("run".equalsIgnoreCase(args[0])) {
                ParallelRunner.main(createArgsWithoutFirstValue(args));
            } else if ("calibrate".equalsIgnoreCase(args[0])) {
                Calibrator.main(createArgsWithoutFirstValue(args));
            } else if ("teardown".equalsIgnoreCase(args[0])) {
                Teardown.main(createArgsWithoutFirstValue(args));
            } else {
                printUsage();
            }
        }else{
            printUsage();
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
    private static void printUsage() {
        System.out.println("The second argument must be calibrate, run or teardown");
    }
}

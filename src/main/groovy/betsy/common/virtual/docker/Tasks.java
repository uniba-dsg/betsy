package betsy.common.virtual.docker;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static betsy.common.config.Configuration.get;

/**
 * @author Christoph Broeker
 * @version 1.0
 *          <p>
 *          <p>
 *          This class has methods to interact with docker console commands.
 */
public class Tasks {

    private static final Logger LOGGER = Logger.getLogger(Tasks.class);
    private static Path docker = Paths.get(get("docker.dir"));

    /**
     * A method to check, if docker is installed.
     *
     * @return Returns true, if docker is installed.
     */
    public static Boolean isDockerInstalled() {
        String[] cmds = {};
        Optional<Scanner> scanner = Optional.ofNullable(doDockerTaskWithOutput(cmds));
        if (scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Usage: docker")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method allows to execute a docker command and returns the output.
     *
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerTaskWithOutput(String... args) {
        Scanner scanner;
        ProcessBuilder builder;
        try {
            builder = new ProcessBuilder("docker");
            List<String> cmds = builder.command();
            Collections.addAll(cmds, args);
            builder.command(cmds);
            builder.directory(docker.toFile());
            LOGGER.info("Execute command: " + Arrays.toString(builder.command().toArray()) + ".");
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" + args[0] + "' command for docker.");
        }
        return scanner;
    }

    /**
     * This method allows to execute a docker command.
     *
     * @param args The arguments for the task.
     */
    public static void doDockerTask(String... args) {
        String[] cmds = args;
        LOGGER.info("Execute command: " + Arrays.toString(cmds) + ".");
        ConsoleTasks.executeOnUnixAndIgnoreError(ConsoleTasks.CliCommand.build("docker").values(cmds));
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build("docker").values(cmds));
    }

    /**
     *
     * This method allows to create an {@link Image} for an engine.
     *
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doEngineImageTask(String... args) {
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_engine_image_cmd");
                builder.directory(docker.toFile());
            }else{
                builder = new ProcessBuilder("docker", "build", "--tag="+args[1], "--build-arg", "engine="+args[2], ".");
                builder.directory(Paths.get(args[0]).toFile());
                if(args.length > 3){
                    System.arraycopy( args, 0, args, 3, args.length-1);
                }else{
                    args = new String[0];
                }
            }
            List<String> cmds = builder.command();
            Collections.addAll(cmds, args);
            builder.command(cmds);
            LOGGER.info("Execute command: " +builder.command()+ " in directory: " +builder.directory()+ ".");
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the 'build' command for docker.");
        }
        return scanner;
    }


    /**
     *
     * This method allows to run or create a {@link Container} with constraints.
     *
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerCreateRunTaskWithConstraints(String... args) {
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                if(args[0].contains("run")){
                    builder = new ProcessBuilder("cmd", "/c", "docker_run_constraints_cmd");
                }else{
                    builder = new ProcessBuilder("cmd", "/c", "docker_create_constraints_cmd");
                }
                List<String> cmds = builder.command();
                int index = cmds.size();
                Collections.addAll(cmds, args);
                cmds.remove(index);
                builder.command(cmds);
            }else{
                builder = new ProcessBuilder("docker", args[0], "--name", args[1], "--device-read-bps=/dev/sda:" +args[2]+ "mb", "--device-write-bps=/dev/sda:" +args[2]+ "mb", "--memory=" +args[3]+ "mb", args[4], "sh", "betsy", args[5], args[6], args[7]);
                if(args.length > 8){
                    List<String> list = builder.command();
                    list.add(args[8]);
                    builder.command(list);
                }
            }
            builder.directory(docker.toFile());
            LOGGER.info("Execute command: " +builder.command()+ " in directory: " +builder.directory()+ ".");
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" +args[0]+ "' command for docker.");
        }
        return scanner;
    }
}

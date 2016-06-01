package betsy.common.virtual.docker;

import betsy.common.tasks.ConsoleTasks;
import betsy.common.virtual.exceptions.DockerException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Christoph Broeker
 * @version 1.0
 *
 *
 * This class has methods to interact with docker console commands.
 *
 */
public class Tasks {

    private static final Logger LOGGER = Logger.getLogger(Tasks.class);
    //TODO: config path

    /**
     *
     * A method to check, if docker is installed.
     *
     * @return Returns true, if docker is installed.
     */
    public static Boolean isDockerInstalled(){
        String[] cmds = {};
        Optional<Scanner> scanner = Optional.ofNullable(doDockerTaskWithOutput(new DockerMachine("test"), cmds));
        if(scanner.isPresent()) {
            while (scanner.get().hasNextLine()) {
                String nextLine = scanner.get().nextLine();
                LOGGER.info(nextLine);
                if (nextLine.contains("Usage: docker")) {
                    return true;
                }
            }
        }
        return  false;
    }

    /**
     *
     * This method allows to execute a task on a {@link DockerMachine} and returns the output.
     *
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerMachineTaskWithOutput(String... args) {
        Scanner scanner = null;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_machine_cmd");
                List<String> cmds = builder.command();
                Collections.addAll(cmds, args);
                builder.command(cmds);
                builder.directory(Paths.get("docker").toFile());
                Process process = builder.start();
                scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
            }
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" + args[0] + "' command for docker.");
        }
        if(scanner == null){
            throw  new DockerException("Couldn't execute the '" + args[0] + "' command for docker.");
        }
        return scanner;
    }

    /**
     *
     * This method allows to execute a task on a {@link DockerMachine}.
     *
     * @param args The arguments for the task.
     */
    public static void doDockerMachineTask(String... args) {
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("docker").toAbsolutePath(), "docker_machine_cmd.bat").values(args));
    }

    /**
     *
     * This method allows to execute a docker command and returns the output.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerTaskWithOutput(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_cmd", dockerMachine.getName());
            }else{
                builder = new ProcessBuilder("docker");
            }
            List<String> cmds = builder.command();
            Collections.addAll(cmds, args);
            builder.command(cmds);
            builder.directory(Paths.get("docker").toFile());
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" + args[0] + "' command for docker.");
        }
        return scanner;
    }

    /**
     *
     * This method allows to execute a docker command.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The arguments for the task.
     */
    public static void doDockerTask(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        String[] cmds = args;
        if(System.getProperty("os.name").contains("Windows")){
            String[] commands = {dockerMachine.getName()};
            cmds = new String[args.length + commands.length];
            System.arraycopy(commands, 0, cmds, 0, commands.length);
            System.arraycopy(args, 0, cmds, commands.length, args.length);
        }
        LOGGER.info("Execute command: " + Arrays.toString(cmds) + " .");
        ConsoleTasks.executeOnWindowsAndIgnoreError(ConsoleTasks.CliCommand.build(Paths.get("docker"), "docker_cmd.bat").values(cmds));
        ConsoleTasks.executeOnUnix(ConsoleTasks.CliCommand.build("docker").values(cmds));
    }

    /**
     *
     * With this method it is possible to create an {@link Image}.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The task to execute.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doImageTask(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_image_cmd", dockerMachine.getName());
                builder.directory(Paths.get("docker").toFile());
            }else{
                builder = new ProcessBuilder("docker", "build", "--tag="+args[1], ".");
                builder.directory(Paths.get(args[0]).toFile());
                if(args.length > 2){
                    System.arraycopy( args, 0, args, 2, args.length-1);
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
            //TODO:
            e.printStackTrace();
            throw new DockerException("Couldn't execute the 'build' command for docker.");
        }
        return scanner;
    }

    /**
     *
     * This method allows to create an {@link Image} for an engine.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doEngineImageTask(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_engine_image_cmd", dockerMachine.getName());
                builder.directory(Paths.get("docker").toFile());
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
     * This method allows to run or create a {@link Container}.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerCreateRunTask(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                builder = new ProcessBuilder("cmd", "/c", "docker_create_run_cmd", dockerMachine.getName());
                List<String> cmds = builder.command();
                Collections.addAll(cmds, args);
                builder.command(cmds);
            }else{
                builder = new ProcessBuilder("docker", args[0]);
                args[0] = "--name";
                List<String> cmds = builder.command();
                Collections.addAll(cmds, args);
                builder.command(cmds);
            }
            builder.directory(Paths.get("docker").toFile());
            LOGGER.info("Execute command: " +builder.command()+ " in directory: " +builder.directory()+ ".");
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" +args[0]+ "' command for docker.");
        }
        return scanner;
    }

    /**
     *
     * This method allows to run or create a {@link Container} with constraints.
     *
     * @param dockerMachine The active {@link DockerMachine}. In case of a linux os not needed.
     * @param args The arguments for the task.
     * @return Returns the scanner to evaluate output.
     */
    public static Scanner doDockerCreateRunTaskWithConstraints(DockerMachine dockerMachine, String... args) {
        Objects.requireNonNull(dockerMachine, "The dockerMachine can't be null.");
        Scanner scanner;
        ProcessBuilder builder;
        try {
            if(System.getProperty("os.name").contains("Windows")){
                if(args[0].contains("run")){
                    builder = new ProcessBuilder("cmd", "/c", "docker_run_constraints_cmd", dockerMachine.getName());
                }else{
                    builder = new ProcessBuilder("cmd", "/c", "docker_create_constraints_cmd", dockerMachine.getName());
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
            builder.directory(Paths.get("docker").toFile());
            LOGGER.info("Execute command: " +builder.command()+ " in directory: " +builder.directory()+ ".");
            Process process = builder.start();
            scanner = new Scanner(process.getInputStream()).useDelimiter("\\Z");
        } catch (IOException e) {
            throw new DockerException("Couldn't execute the '" +args[0]+ "' command for docker.");
        }
        return scanner;
    }
}
